package com.edusync.service;

import com.edusync.common.enums.GroupRole;
import com.edusync.common.enums.GroupVisibility;
import com.edusync.common.enums.JoinRequestStatus;
import com.edusync.exception.AppException;
import com.edusync.model.dto.request.CreateGroupRequest;
import com.edusync.model.dto.response.GroupResponse;
import com.edusync.model.entity.Group;
import com.edusync.model.entity.GroupJoinRequest;
import com.edusync.model.entity.GroupMember;
import com.edusync.model.entity.User;
import com.edusync.repository.GroupJoinRequestRepository;
import com.edusync.repository.GroupMemberRepository;
import com.edusync.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupJoinRequestRepository groupJoinRequestRepository;

    @Transactional
    public GroupResponse createGroup(CreateGroupRequest request, User currentUser) {
        String joinCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Group group = Group.builder()
                .name(request.getName())
                .description(request.getDescription())
                .visibility(request.getVisibility() != null ? request.getVisibility() : GroupVisibility.PUBLIC)
                .joinCode(joinCode)
                .owner(currentUser)
                .build();
        
        Group savedGroup = groupRepository.save(group);

        GroupMember member = GroupMember.builder()
                .id(new GroupMember.GroupMemberId(savedGroup.getId(), currentUser.getId()))
                .group(savedGroup)
                .user(currentUser)
                .role(GroupRole.OWNER)
                .build();
        
        groupMemberRepository.save(member);

        return mapToResponse(savedGroup);
    }

    @Transactional
    public String joinGroup(Long groupId, User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException("Group not found", HttpStatus.NOT_FOUND));

        if (groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUser.getId())) {
            throw new AppException("User already a member of this group", HttpStatus.BAD_REQUEST);
        }

        if (group.isSecret()) {
            throw new AppException("This group is secret. You need a join code to explicitly enter.", HttpStatus.FORBIDDEN);
        }

        if (group.isPrivate()) {
            if (groupJoinRequestRepository.existsByGroupIdAndUserIdAndStatus(
                    groupId, currentUser.getId(), JoinRequestStatus.PENDING)) {
                throw new AppException("Join request already pending", HttpStatus.BAD_REQUEST);
            }

            GroupJoinRequest request = GroupJoinRequest.builder()
                    .group(group)
                    .user(currentUser)
                    .status(JoinRequestStatus.PENDING)
                    .build();
            groupJoinRequestRepository.save(request);
            
            return "Join request sent. Please wait for an admin to approve.";
        } else {
            GroupMember member = GroupMember.builder()
                    .id(new GroupMember.GroupMemberId(groupId, currentUser.getId()))
                    .group(group)
                    .user(currentUser)
                    .role(GroupRole.MEMBER)
                    .build();

            groupMemberRepository.save(member);
            return "Joined group successfully";
        }
    }

    @Transactional
    public void joinGroupByCode(String joinCode, User currentUser) {
        Group group = groupRepository.findByJoinCode(joinCode)
                .orElseThrow(() -> new AppException("Invalid join code", HttpStatus.NOT_FOUND));

        if (groupMemberRepository.existsByGroupIdAndUserId(group.getId(), currentUser.getId())) {
            throw new AppException("User already a member of this group", HttpStatus.BAD_REQUEST);
        }

        GroupMember member = GroupMember.builder()
                .id(new GroupMember.GroupMemberId(group.getId(), currentUser.getId()))
                .group(group)
                .user(currentUser)
                .role(GroupRole.MEMBER)
                .build();

        groupMemberRepository.save(member);
    }

    public List<GroupResponse> getUserGroups(Long userId) {
        return groupMemberRepository.findGroupsByUserIdWithDetails(userId).stream()
                .map(member -> mapToResponse(member.getGroup()))
                .collect(Collectors.toList());
    }

    public List<GroupResponse> getDiscoveryGroups(Long userId) {
        return groupRepository.findGroupsUserNotJoined(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void leaveGroup(Long groupId, User currentUser) {
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, currentUser.getId())
                .orElseThrow(() -> new AppException("You are not a member of this group", HttpStatus.NOT_FOUND));

        if (member.getRole() == GroupRole.OWNER) {
            throw new AppException("Owner cannot leave the group. Delete the group instead or transfer ownership.", HttpStatus.BAD_REQUEST);
        }

        groupMemberRepository.delete(member);
    }

    @Transactional
    public void deleteGroup(Long groupId, User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException("Group not found", HttpStatus.NOT_FOUND));

        if (!group.getOwner().getId().equals(currentUser.getId())) {
            throw new AppException("Only the group owner can delete the group", HttpStatus.FORBIDDEN);
        }

        groupRepository.delete(group);
    }

    @Transactional
    public void approveJoinRequest(Long requestId, User currentUser) {
        GroupJoinRequest request = groupJoinRequestRepository.findById(requestId)
                .orElseThrow(() -> new AppException("Join request not found", HttpStatus.NOT_FOUND));

        validateAdminOrOwner(request.getGroup().getId(), currentUser.getId());

        if (request.getStatus() != JoinRequestStatus.PENDING) {
            throw new AppException("Request is already processed", HttpStatus.BAD_REQUEST);
        }

        request.setStatus(JoinRequestStatus.APPROVED);
        groupJoinRequestRepository.save(request);

        GroupMember newMember = GroupMember.builder()
                .id(new GroupMember.GroupMemberId(request.getGroup().getId(), request.getUser().getId()))
                .group(request.getGroup())
                .user(request.getUser())
                .role(GroupRole.MEMBER)
                .build();

        groupMemberRepository.save(newMember);
    }

    @Transactional
    public void rejectJoinRequest(Long requestId, User currentUser) {
        GroupJoinRequest request = groupJoinRequestRepository.findById(requestId)
                .orElseThrow(() -> new AppException("Join request not found", HttpStatus.NOT_FOUND));

        validateAdminOrOwner(request.getGroup().getId(), currentUser.getId());

        if (request.getStatus() != JoinRequestStatus.PENDING) {
            throw new AppException("Request is already processed", HttpStatus.BAD_REQUEST);
        }

        request.setStatus(JoinRequestStatus.REJECTED);
        groupJoinRequestRepository.save(request);
    }

    private void validateAdminOrOwner(Long groupId, Long userId) {
        GroupMember adminMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new AppException("You are not a member of this group", HttpStatus.FORBIDDEN));

        if (adminMember.getRole() != GroupRole.ADMIN && adminMember.getRole() != GroupRole.OWNER) {
            throw new AppException("You do not have permission to perform this action", HttpStatus.FORBIDDEN);
        }
    }

    private GroupResponse mapToResponse(Group group) {
        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .ownerName(group.getOwner().getUsername())
                .visibility(group.getVisibility())
                .joinCode(group.getJoinCode())
                .createdAt(group.getCreatedAt())
                .build();
    }
}

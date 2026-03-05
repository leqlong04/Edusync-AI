package com.edusync.service;

import com.edusync.common.enums.GroupRole;
import com.edusync.exception.AppException;
import com.edusync.model.dto.request.CreateGroupRequest;
import com.edusync.model.dto.response.GroupResponse;
import com.edusync.model.entity.Group;
import com.edusync.model.entity.GroupMember;
import com.edusync.model.entity.User;
import com.edusync.repository.GroupMemberRepository;
import com.edusync.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public GroupResponse createGroup(CreateGroupRequest request, User currentUser) {
        Group group = Group.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(currentUser)
                .build();
        
        Group savedGroup = groupRepository.save(group);

        // Auto join as OWNER
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
    public void joinGroup(Long groupId, User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException("Group not found", HttpStatus.NOT_FOUND));

        if (groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUser.getId())) {
            throw new AppException("User already a member of this group", HttpStatus.BAD_REQUEST);
        }

        GroupMember member = GroupMember.builder()
                .id(new GroupMember.GroupMemberId(groupId, currentUser.getId()))
                .group(group)
                .user(currentUser)
                .role(GroupRole.MEMBER)
                .build();

        groupMemberRepository.save(member);
    }

    public List<GroupResponse> getUserGroups(Long userId) {
        return groupMemberRepository.findByUserId(userId).stream()
                .map(member -> mapToResponse(member.getGroup()))
                .collect(Collectors.toList());
    }

    public List<GroupResponse> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private GroupResponse mapToResponse(Group group) {
        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .ownerName(group.getOwner().getUsername())
                .createdAt(group.getCreatedAt())
                .build();
    }
}

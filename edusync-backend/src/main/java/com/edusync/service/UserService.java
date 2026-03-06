package com.edusync.service;

import com.edusync.model.dto.response.UserProfileResponse;
import com.edusync.model.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public UserProfileResponse getUserProfile(User currentUser) {
        return UserProfileResponse.builder()
                .id(currentUser.getId())
                .username(currentUser.getUsername())
                .email(currentUser.getEmail())
                .reputationScore(currentUser.getReputationScore())
                .createdAt(currentUser.getCreatedAt())
                .build();
    }
}

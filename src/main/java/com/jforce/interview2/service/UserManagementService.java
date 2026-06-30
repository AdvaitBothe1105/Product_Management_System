package com.jforce.interview2.service;

import com.jforce.interview2.dto.UserResponse;
import com.jforce.interview2.model.Role;
import com.jforce.interview2.model.User;
import com.jforce.interview2.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserManagementService {
    private final UserRepo userRepo;

    private UserResponse toDto(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.isEnabled(),
                user.getRole().name()  // adjust to your actual Role structure
        );
    }

    public List<UserResponse> getAllUsers() {
        return userRepo.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public UserResponse updateUserRole(Integer userId, String roleName) {
        User user = userRepo.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found: " + userId));

        // assuming Role is a separate entity — adjust based on your actual setup

        user.setRole(Role.valueOf(roleName));
        return toDto(userRepo.save(user));
    }

    public UserResponse toggleUserStatus(Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found: " + userId));

        user.setEnabled(!user.isEnabled());
        return toDto(userRepo.save(user));
    }

}

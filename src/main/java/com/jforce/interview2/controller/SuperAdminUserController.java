package com.jforce.interview2.controller;

import com.jforce.interview2.dto.UserResponse;
import com.jforce.interview2.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/super-admin/users")
@RequiredArgsConstructor
public class SuperAdminUserController {

    private final UserManagementService userManagementService;

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userManagementService.getAllUsers();
    }

    @PatchMapping("/{id}/role")
    public UserResponse updateRole(
            @PathVariable Integer id,
            @RequestParam String role) {
        return userManagementService.updateUserRole(id, role);
    }

    @PatchMapping("/{id}/toggle")
    public UserResponse toggleStatus(@PathVariable Integer id) {
        return userManagementService.toggleUserStatus(id);
    }

}

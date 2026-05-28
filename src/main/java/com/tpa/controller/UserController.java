package com.tpa.controller;

import com.tpa.dto.response.auth.ApiResponse;
import com.tpa.dto.response.user.UserResponse;
import com.tpa.entity.User;
import com.tpa.exception.NoResourceFoundException;
import com.tpa.mapper.UserMapper;
import com.tpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> getUserProfile(Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new NoResourceFoundException("User not found"));
        return ResponseEntity.ok(new ApiResponse<>(true, "Profile fetched successfully", userMapper.toUserResponse(user), 200));
    }
}

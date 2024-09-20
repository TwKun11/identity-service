package com.identity_user.identtity_user.controller;

import com.identity_user.identtity_user.configuration.SecurityConfig;
import com.identity_user.identtity_user.dto.request.ApiResponse;
import com.identity_user.identtity_user.dto.request.UserCreationRequest;
import com.identity_user.identtity_user.dto.request.UserUpdateRequest;
import com.identity_user.identtity_user.dto.response.UserResponse;
import com.identity_user.identtity_user.entity.User;
import com.identity_user.identtity_user.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class userController {
    @Autowired
    private UserService userService;

    @PostMapping
    ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request) {
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createRequest(request));
        return apiResponse;
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }
    @GetMapping
    ApiResponse<List<UserResponse>> getUsers(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }
    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo(){
        return ApiResponse.<UserResponse>builder().result(userService.getMyInfor()).build();
    }
    @PutMapping("/{userId}")
    UserResponse updateUser(@PathVariable("userId") String userId,@RequestBody UserUpdateRequest request){
        return userService.updateUser(userId, request);
    }
}

package com.identity_user.identtity_user.mapper;

import com.identity_user.identtity_user.dto.request.UserCreationRequest;
import com.identity_user.identtity_user.dto.request.UserUpdateRequest;
import com.identity_user.identtity_user.dto.response.UserResponse;
import com.identity_user.identtity_user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.lang.annotation.Target;

@Mapper(componentModel = "spring")
public interface UserMapper {
  public User toUser(UserCreationRequest request);

  UserResponse toUserResponse (User user);

  @Mapping(target = "roles",ignore = true)
  void updateUser(@MappingTarget User user, UserUpdateRequest request);
}

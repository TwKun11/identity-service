package com.identity_user.identtity_user.mapper;

import com.identity_user.identtity_user.dto.request.PermissionRequest;
import com.identity_user.identtity_user.dto.request.UserCreationRequest;
import com.identity_user.identtity_user.dto.request.UserUpdateRequest;
import com.identity_user.identtity_user.dto.response.PermissionResponse;
import com.identity_user.identtity_user.dto.response.UserResponse;
import com.identity_user.identtity_user.entity.Permission;
import com.identity_user.identtity_user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
  public Permission toPermission(PermissionRequest request);

  public PermissionResponse toPermissionResponse(Permission permission);
}

package com.identity_user.identtity_user.mapper;

import com.identity_user.identtity_user.dto.request.PermissionRequest;
import com.identity_user.identtity_user.dto.request.RoleRequest;
import com.identity_user.identtity_user.dto.response.PermissionResponse;
import com.identity_user.identtity_user.dto.response.RoleResponse;
import com.identity_user.identtity_user.entity.Permission;
import com.identity_user.identtity_user.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
  @Mapping(target = "permissions", ignore = true)
  Role toRole(RoleRequest request);

  RoleResponse toRoleResponse(Role role);
}

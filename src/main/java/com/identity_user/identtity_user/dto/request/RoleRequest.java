package com.identity_user.identtity_user.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)



public class RoleRequest {
    String name;
    String description;
    Set<String> permissions;
}

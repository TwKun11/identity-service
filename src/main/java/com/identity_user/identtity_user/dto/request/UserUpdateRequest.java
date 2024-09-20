package com.identity_user.identtity_user.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {

     String password;
     String firstName;
     String lastName;
     LocalDate dob;

     List<String> roles;
}

package com.identity_user.identtity_user.dto.request;

import com.identity_user.identtity_user.validation.DobConstraint;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 3, message = "USERNAME_INVALID")
    String userName;

     String password;
     String firstName;
     String lastName;
     @DobConstraint(min =16,message = "INVALID_DOB")
     LocalDate dob;


}

package com.identity_user.identtity_user.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue( strategy = GenerationType.UUID)
     String id;
     String userName;
     String password;
     String firstName;
     String lastName;
     LocalDate dob;

     @ManyToMany
     Set<Role> roles;

}

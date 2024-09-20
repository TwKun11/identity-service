package com.identity_user.identtity_user.repository;

import com.identity_user.identtity_user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUserName(String username);
    Optional<User> findByUserName(String username);
}

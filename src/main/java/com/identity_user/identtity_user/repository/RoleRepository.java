package com.identity_user.identtity_user.repository;

import com.identity_user.identtity_user.entity.Permission;
import com.identity_user.identtity_user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {


}

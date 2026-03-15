package com.room.management.repository;

import com.room.management.entity.auth.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleNameAndIsActiveTrue(String roleName);

    boolean existsByRoleName(String roleName);
}

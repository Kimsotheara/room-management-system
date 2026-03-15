package com.room.management.repository;

import com.room.management.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
            SELECT u FROM User u
            LEFT JOIN FETCH u.userRoles ur
            LEFT JOIN FETCH ur.role r
            LEFT JOIN FETCH r.rolePermissions rp
            LEFT JOIN FETCH rp.permission p
            WHERE u.username = :username AND u.isActive = true
            """)
    Optional<User> findActiveUserWithRolesByUsername(@Param("username") String username);

    @Query("""
            SELECT u FROM User u
            LEFT JOIN FETCH u.userRoles ur
            LEFT JOIN FETCH ur.role r
            LEFT JOIN FETCH r.rolePermissions rp
            LEFT JOIN FETCH rp.permission p
            WHERE u.userId = :userId AND u.isActive = true
            """)
    Optional<User> findActiveUserWithRolesById(@Param("userId") Long userId);

    Optional<User> findByUsernameAndIsActiveTrue(String username);

    Optional<User> findByEmailAndIsActiveTrue(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}

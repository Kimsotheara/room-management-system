package com.room.management.repository;

import com.room.management.entity.auth.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Query("SELECT ur FROM UserRole ur WHERE ur.user.userId = :userId AND ur.role.roleId = :roleId AND ur.isActive = true")
    Optional<UserRole> findActiveByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Query("SELECT COUNT(ur) > 0 FROM UserRole ur WHERE ur.user.userId = :userId AND ur.role.roleId = :roleId AND ur.isActive = true")
    boolean existsActiveByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Modifying
    @Transactional
    @Query("UPDATE UserRole ur SET ur.isActive = false WHERE ur.user.userId = :userId AND ur.role.roleId = :roleId")
    void deactivateByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);
}

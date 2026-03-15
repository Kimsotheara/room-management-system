package com.room.management.repository;

import com.room.management.dto.request.PageAbleRequest;
import com.room.management.dto.request.UserRequestDto;
import com.room.management.entity.auth.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

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

    @Query("""
            SELECT u FROM User u
            WHERE (:username = '' OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%')))
              AND (:email = '' OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))
              AND (:fullName = '' OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :fullName, '%')))
              AND (:phoneNumber = '' OR u.phoneNumber LIKE CONCAT('%', :phoneNumber, '%'))
              AND (cast(:isActive as Boolean) IS NULL OR u.isActive = :isActive)
              AND (cast(:roleId as Long) IS NULL OR EXISTS (
                    SELECT ur FROM UserRole ur
                    WHERE ur.user = u AND ur.role.roleId = :roleId AND ur.isActive = true
              ))
            """)
    Page<User> executeFilterQuery(
            @Param("username") String username,
            @Param("email") String email,
            @Param("fullName") String fullName,
            @Param("phoneNumber") String phoneNumber,
            @Param("isActive") Boolean isActive,
            @Param("roleId") Long roleId,
            Pageable pageable);

    default Page<User> findAllWithFilter(PageAbleRequest<UserRequestDto> request) {
        UserRequestDto f = request.getParameter();
        return executeFilterQuery(
                f != null && StringUtils.hasText(f.getUsername())    ? f.getUsername()    : "",
                f != null && StringUtils.hasText(f.getEmail())       ? f.getEmail()       : "",
                f != null && StringUtils.hasText(f.getFullName())    ? f.getFullName()    : "",
                f != null && StringUtils.hasText(f.getPhoneNumber()) ? f.getPhoneNumber() : "",
                f != null ? f.getIsActive() : null,
                f != null ? f.getRoleId()   : null,
                request.getPageAble()
        );
    }

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}

package com.room.management.repository;

import com.room.management.entity.auth.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    @Query("""
            SELECT t FROM UserToken t
            JOIN FETCH t.user u
            LEFT JOIN FETCH u.userRoles ur
            LEFT JOIN FETCH ur.role r
            LEFT JOIN FETCH r.rolePermissions rp
            LEFT JOIN FETCH rp.permission p
            WHERE t.tokenValue = :tokenValue AND t.isRevoked = false
            """)
    Optional<UserToken> findValidTokenWithUserAndRoles(@Param("tokenValue") String tokenValue);

    @Query("""
            SELECT t FROM UserToken t
            WHERE t.user.userId = :userId
              AND t.isRevoked = false
              AND t.expiryDate > :now
            """)
    List<UserToken> findActiveTokensByUserId(@Param("userId") Long userId,
                                              @Param("now") LocalDateTime now);

    @Query("""
            SELECT t FROM UserToken t
            WHERE t.user.userId = :userId
              AND t.tokenType = :tokenType
              AND t.isRevoked = false
              AND t.expiryDate > :now
            """)
    List<UserToken> findActiveTokensByUserIdAndType(@Param("userId") Long userId,
                                                     @Param("tokenType") String tokenType,
                                                     @Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    @Query("UPDATE UserToken t SET t.isRevoked = true WHERE t.user.userId = :userId")
    void revokeAllTokensByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE UserToken t SET t.isRevoked = true WHERE t.user.userId = :userId AND t.tokenType = :tokenType")
    void revokeTokensByUserIdAndType(@Param("userId") Long userId, @Param("tokenType") String tokenType);

    @Modifying
    @Transactional
    @Query("UPDATE UserToken t SET t.isRevoked = true WHERE t.tokenValue = :tokenValue")
    void revokeByTokenValue(@Param("tokenValue") String tokenValue);

    @Query("""
            SELECT t FROM UserToken t
            WHERE t.permissionSnapshotAt < :cutoffTime
              AND t.isRevoked = false
              AND t.expiryDate > :now
            """)
    List<UserToken> findTokensWithOutdatedPermissions(@Param("cutoffTime") LocalDateTime cutoffTime,
                                                       @Param("now") LocalDateTime now);
}

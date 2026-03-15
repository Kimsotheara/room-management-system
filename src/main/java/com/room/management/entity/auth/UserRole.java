package com.room.management.entity.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
    name = "AUTH_USER_ROLES",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "role_id"})
    }
)
public class UserRole extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_role_id")
    private Long userRoleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private Boolean isActive = true;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isEffective() {
        return Boolean.TRUE.equals(isActive) && !isExpired()
                && Boolean.TRUE.equals(role.getIsActive());
    }
}

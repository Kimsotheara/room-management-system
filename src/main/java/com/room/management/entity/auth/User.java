package com.room.management.entity.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "AUTH_USERS")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", unique = true, nullable = false, length = 100)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", unique = true, length = 200)
    private String email;

    @Column(name = "full_name", length = 200)
    private String fullName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserRole> userRoles;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<UserToken> userTokens;

    public Set<Role> getActiveRoles() {
        if (userRoles == null) return Set.of();
        return userRoles.stream()
                .filter(UserRole::isEffective)
                .map(UserRole::getRole)
                .collect(Collectors.toSet());
    }

    public boolean hasRole(String roleName) {
        return getActiveRoles().stream()
                .anyMatch(r -> r.getRoleName().equalsIgnoreCase(roleName));
    }

    public boolean hasAnyRole(String... roleNames) {
        Set<String> roleNamesLower = Set.of(roleNames);
        return getActiveRoles().stream()
                .anyMatch(r -> roleNamesLower.contains(r.getRoleName().toLowerCase()));
    }

    public boolean canAccessResource(String actionCode, String permissionCode) {
        return getActiveRoles().stream()
                .anyMatch(r -> r.hasPermissionWithAction(permissionCode, actionCode));
    }

    public Set<String> getAllPermissionCodes() {
        if (userRoles == null) return Set.of();
        return userRoles.stream()
                .filter(UserRole::isEffective)
                .map(UserRole::getRole)
                .flatMap(r -> r.getActivePermissions().stream())
                .map(Permission::getPermissionCode)
                .collect(Collectors.toSet());
    }
}

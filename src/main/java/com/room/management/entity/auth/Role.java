package com.room.management.entity.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "AUTH_ROLES")
public class Role extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "role_name", unique = true, nullable = false, length = 100)
    private String roleName;

    @Column(name = "role_description", length = 500)
    private String roleDescription;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private Set<UserRole> userRoles;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<RolePermission> rolePermissions;

    public Set<Permission> getActivePermissions() {
        if (rolePermissions == null) return Set.of();
        return rolePermissions.stream()
                .filter(rp -> Boolean.TRUE.equals(rp.getIsActive()))
                .map(RolePermission::getPermission)
                .filter(p -> Boolean.TRUE.equals(p.getIsActive()))
                .collect(Collectors.toSet());
    }

    public Set<RolePermission> getActiveRolePermissions() {
        if (rolePermissions == null) return Set.of();
        return rolePermissions.stream()
                .filter(rp -> Boolean.TRUE.equals(rp.getIsActive()))
                .collect(Collectors.toSet());
    }

    public boolean hasPermission(String permissionCode) {
        return getActivePermissions().stream()
                .anyMatch(p -> p.getPermissionCode().equals(permissionCode));
    }

    public boolean hasPermissionWithAction(String permissionCode, String actionCode) {
        return getActiveRolePermissions().stream()
                .filter(rp -> rp.getPermission().getPermissionCode().equals(permissionCode))
                .anyMatch(rp -> rp.isActionAllowed(actionCode));
    }
}

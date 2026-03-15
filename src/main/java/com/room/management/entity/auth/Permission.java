package com.room.management.entity.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "AUTH_PERMISSIONS")
public class Permission extends BaseEntity {

    @Id
    @Column(name = "permission_code", length = 50)
    private String permissionCode;

    @Column(name = "permission_name", unique = true, nullable = false, length = 200)
    private String permissionName;

    @Column(name = "permission_description", length = 500)
    private String permissionDescription;

    @Column(name = "permission_level")
    private Integer permissionLevel;

    @Column(name = "is_system_permission", columnDefinition = "boolean default false")
    private Boolean isSystemPermission = false;

    @Column(name = "is_inheritable", columnDefinition = "boolean default true")
    private Boolean isInheritable = true;

    @Column(name = "is_delegatable", columnDefinition = "boolean default false")
    private Boolean isDelegatable = false;

    @Column(name = "requires_approval", columnDefinition = "boolean default false")
    private Boolean requiresApproval = false;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private Boolean isActive = true;

    @Column(name = "available_action_codes", length = 500)
    private String availableActionCodes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_permission_code")
    private Permission parentPermission;

    @OneToMany(mappedBy = "parentPermission", fetch = FetchType.LAZY)
    private List<Permission> childPermissions;

    @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY)
    private Set<PermissionActionResource> permissionActionResources;

    public List<String> getAvailableActionCodesList() {
        if (availableActionCodes == null || availableActionCodes.isBlank()) {
            return List.of();
        }
        return Arrays.stream(availableActionCodes.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public boolean supportsAction(String actionCode) {
        List<String> codes = getAvailableActionCodesList();
        return codes.isEmpty() || codes.contains(actionCode.toUpperCase());
    }
}

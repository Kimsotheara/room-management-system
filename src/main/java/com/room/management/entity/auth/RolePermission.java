package com.room.management.entity.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(
    name = "AUTH_ROLE_PERMISSIONS",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"role_id", "permission_code"})
    }
)
public class RolePermission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_permission_id")
    private Long rolePermissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "permission_code", nullable = false)
    private Permission permission;

    @Column(name = "allowed_action_codes", length = 500)
    private String allowedActionCodes;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private Boolean isActive = true;

    public boolean hasFullAccess() {
        return allowedActionCodes == null || allowedActionCodes.isBlank();
    }

    public boolean isActionAllowed(String actionCode) {
        if (hasFullAccess()) return true;
        List<String> codes = getAllowedActionCodesList();
        return codes.contains(actionCode.toUpperCase());
    }

    public List<String> getAllowedActionCodesList() {
        if (allowedActionCodes == null || allowedActionCodes.isBlank()) {
            return List.of();
        }
        return Arrays.stream(allowedActionCodes.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public void setAllowedActionCodesList(List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            this.allowedActionCodes = null;
        } else {
            this.allowedActionCodes = String.join(",", codes);
        }
    }

    public boolean isReadOnly() {
        List<String> codes = getAllowedActionCodesList();
        return !codes.isEmpty() && codes.stream().allMatch(Action.READ_ONLY_ACTIONS::contains);
    }

    public boolean hasWriteAccess() {
        if (hasFullAccess()) return true;
        return getAllowedActionCodesList().stream().anyMatch(Action.WRITE_ACTIONS::contains);
    }

    public Set<String> getAllowedActions() {
        if (hasFullAccess()) return Set.of("*");
        return Set.copyOf(getAllowedActionCodesList());
    }

    public String getAccessSummary() {
        if (hasFullAccess()) return "FULL_ACCESS";
        return String.join(", ", getAllowedActionCodesList());
    }
}

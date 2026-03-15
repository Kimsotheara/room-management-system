package com.room.management.entity.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "AUTH_USER_TOKENS")
public class UserToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long tokenId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_value", unique = true, length = 2000, nullable = false)
    private String tokenValue;

    @Column(name = "token_type", length = 20)
    private String tokenType;

    @Column(name = "device_id", length = 200)
    private String deviceId;

    @Column(name = "device_name", length = 200)
    private String deviceName;

    @Column(name = "device_type", length = 50)
    private String deviceType;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "is_revoked", columnDefinition = "boolean default false")
    private boolean isRevoked = false;

    @Column(name = "permission_codes", length = 2000)
    private String permissionCodes;

    @Column(name = "permission_snapshot_at")
    private LocalDateTime permissionSnapshotAt;

    public List<String> getPermissionCodesList() {
        if (permissionCodes == null || permissionCodes.isBlank()) {
            return List.of();
        }
        return Arrays.stream(permissionCodes.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public void setPermissionCodesList(List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            this.permissionCodes = null;
        } else {
            this.permissionCodes = String.join(",", codes);
        }
        this.permissionSnapshotAt = LocalDateTime.now();
    }

    public boolean hasPermissionCode(String code) {
        return getPermissionCodesList().contains(code);
    }

    public boolean isExpired() {
        return expiryDate != null && LocalDateTime.now().isAfter(expiryDate);
    }

    public boolean isValid() {
        return !isRevoked && !isExpired();
    }

    public boolean isPermissionSnapshotRecent(int hoursThreshold) {
        if (permissionSnapshotAt == null) return false;
        return LocalDateTime.now().isBefore(permissionSnapshotAt.plusHours(hoursThreshold));
    }
}

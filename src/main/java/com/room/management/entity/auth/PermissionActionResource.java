package com.room.management.entity.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
    name = "AUTH_PERMISSION_ACTION_RESOURCES",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"permission_code", "action_code", "resource_id"})
    }
)
public class PermissionActionResource extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_action_resource_id")
    private Long permissionActionResourceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_code", nullable = false)
    private Permission permission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_code", nullable = false)
    private Action action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    @Column(name = "priority")
    private Integer priority = 1;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private Boolean isActive = true;

    public boolean isAccessAllowed() {
        return Boolean.TRUE.equals(isActive);
    }

    public boolean matchesEndpoint(String endpoint) {
        return resource != null && resource.getBaseEndpoint() != null
                && endpoint.startsWith(resource.getBaseEndpoint());
    }

    public boolean matchesEndpointWithMethod(String endpoint, String httpMethod) {
        if (resource == null) return false;
        return resource.matchesEndpoint(endpoint, httpMethod);
    }
}

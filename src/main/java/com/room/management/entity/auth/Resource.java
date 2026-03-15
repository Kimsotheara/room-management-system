package com.room.management.entity.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "AUTH_RESOURCES")
public class Resource extends BaseEntity {

    @Id
    @Column(name = "resource_id", length = 100)
    private String resourceId;

    @Column(name = "resource_name", unique = true, nullable = false, length = 200)
    private String resourceName;

    @Column(name = "resource_description", length = 500)
    private String resourceDescription;

    @Column(name = "resource_category", length = 100)
    private String resourceCategory;

    @Column(name = "controller_class", length = 200)
    private String controllerClass;

    @Column(name = "method_name", length = 200)
    private String methodName;

    @Column(name = "http_method", length = 10)
    private String httpMethod;

    @Column(name = "base_endpoint", length = 500)
    private String baseEndpoint;

    @Column(name = "pattern", length = 500)
    private String pattern;

    @Column(name = "resource_level")
    private Integer resourceLevel;

    @Column(name = "is_core_resource", columnDefinition = "boolean default false")
    private Boolean isCoreResource = false;

    @Column(name = "is_public_resource", columnDefinition = "boolean default false")
    private Boolean isPublicResource = false;

    @Column(name = "requires_ownership", columnDefinition = "boolean default false")
    private Boolean requiresOwnership = false;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_resource_id")
    private Resource parentResource;

    @OneToMany(mappedBy = "parentResource", fetch = FetchType.LAZY)
    private Set<Resource> childResources;

    @OneToMany(mappedBy = "resource", fetch = FetchType.LAZY)
    private Set<PermissionActionResource> permissionActionResources;

    public boolean matchesEndpoint(String url, String method) {
        if (httpMethod != null && !httpMethod.equalsIgnoreCase(method)) {
            return false;
        }
        if (pattern != null && !pattern.isBlank()) {
            String regexPattern = pattern.replaceAll("\\{[^/]+}", "[^/]+");
            return url.matches(regexPattern);
        }
        if (baseEndpoint != null) {
            return url.startsWith(baseEndpoint) || url.equals(baseEndpoint);
        }
        return false;
    }

    public boolean supportsPublicAccess() {
        return Boolean.TRUE.equals(isPublicResource);
    }

    public boolean needsOwnershipCheck() {
        return Boolean.TRUE.equals(requiresOwnership);
    }
}

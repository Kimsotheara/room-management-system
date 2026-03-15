package com.room.management.security.service;

import com.room.management.entity.auth.PermissionActionResource;
import com.room.management.entity.auth.Role;
import com.room.management.entity.auth.RolePermission;
import com.room.management.entity.auth.UserToken;
import com.room.management.service.auth.InMemoryResourceStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionValidationService {

    private final InMemoryResourceStore resourceStore;

    public boolean hasPermission(UserToken userToken, String requestUri, String httpMethod) {
        if (resourceStore.isPublicResource(requestUri, httpMethod)) {
            log.debug("Public resource access granted: {} {}", httpMethod, requestUri);
            return true;
        }

        List<PermissionActionResource> matchingPARs = resourceStore.findMatchingPARs(requestUri, httpMethod);

        if (matchingPARs.isEmpty()) {
            log.debug("No resource mapping found for {} {} — token presence is sufficient", httpMethod, requestUri);
            return true;
        }

        Set<Role> userRoles = userToken.getUser().getActiveRoles();

        for (PermissionActionResource par : matchingPARs) {
            if (!Boolean.TRUE.equals(par.getIsActive())) continue;

            String requiredPermissionCode = par.getPermission().getPermissionCode();
            String requiredActionCode = par.getAction().getActionCode();

            for (Role role : userRoles) {
                for (RolePermission rp : role.getActiveRolePermissions()) {
                    if (!rp.getPermission().getPermissionCode().equals(requiredPermissionCode)) continue;

                    if (rp.isActionAllowed(requiredActionCode)) {
                        log.debug("Access granted: role={}, permission={}, action={}",
                                role.getRoleName(), requiredPermissionCode, requiredActionCode);
                        return true;
                    }
                }
            }
        }

        log.debug("Access denied for {} {} — no matching role-permission-action combination",
                httpMethod, requestUri);
        return false;
    }
}

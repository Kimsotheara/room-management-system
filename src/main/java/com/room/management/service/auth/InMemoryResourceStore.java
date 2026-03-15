package com.room.management.service.auth;

import com.room.management.entity.auth.PermissionActionResource;
import com.room.management.entity.auth.Resource;
import com.room.management.repository.PermissionActionResourceRepository;
import com.room.management.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryResourceStore {

    private final ResourceRepository resourceRepository;
    private final PermissionActionResourceRepository permissionActionResourceRepository;

    private final Map<String, Resource> resourcesById = new ConcurrentHashMap<>();
    private final List<Resource> publicResources = new CopyOnWriteArrayList<>();
    private final List<PermissionActionResource> allPARs = new CopyOnWriteArrayList<>();

    public void refresh() {
        log.info("Refreshing in-memory resource store...");
        resourcesById.clear();
        publicResources.clear();
        allPARs.clear();

        List<Resource> resources = resourceRepository.findAllByIsActiveTrue();
        resources.forEach(r -> {
            resourcesById.put(r.getResourceId(), r);
            if (Boolean.TRUE.equals(r.getIsPublicResource())) {
                publicResources.add(r);
            }
        });

        List<PermissionActionResource> pars = permissionActionResourceRepository.findAllActiveWithDetails();
        allPARs.addAll(pars);

        log.info("Resource store loaded: {} resources, {} permission-action-resources",
                resources.size(), pars.size());
    }

    public boolean isPublicResource(String uri, String method) {
        return publicResources.stream()
                .anyMatch(r -> r.matchesEndpoint(uri, method));
    }

    public List<PermissionActionResource> findMatchingPARs(String uri, String method) {
        return allPARs.stream()
                .filter(par -> par.getResource().matchesEndpoint(uri, method))
                .toList();
    }

    public Resource findById(String resourceId) {
        return resourcesById.get(resourceId);
    }

    public void addResource(Resource resource) {
        resourcesById.put(resource.getResourceId(), resource);
        if (Boolean.TRUE.equals(resource.getIsPublicResource())) {
            publicResources.removeIf(r -> r.getResourceId().equals(resource.getResourceId()));
            publicResources.add(resource);
        }
    }
}

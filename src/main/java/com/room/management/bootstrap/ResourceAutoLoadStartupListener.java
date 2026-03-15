package com.room.management.bootstrap;

import com.room.management.annotation.AuthResource;
import com.room.management.entity.auth.Resource;
import com.room.management.repository.ResourceRepository;
import com.room.management.service.auth.InMemoryResourceStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceAutoLoadStartupListener implements ApplicationListener<ApplicationReadyEvent> {

    private final ApplicationContext applicationContext;
    private final ResourceRepository resourceRepository;
    private final InMemoryResourceStore resourceStore;

    @Value("${app.resource.auto-load.enabled:true}")
    private boolean autoLoadEnabled;

    @Value("${app.resource.auto-load.update-existing:true}")
    private boolean updateExisting;

    @Value("${app.resource.auto-load.fail-on-error:false}")
    private boolean failOnError;

    @Value("${app.resource.auto-load.default-resource-category:API_ENDPOINT}")
    private String defaultCategory;

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        if (!autoLoadEnabled) {
            log.info("Resource auto-load is disabled");
            resourceStore.refresh();
            return;
        }

        try {
            loadResourcesFromControllers();
            resourceStore.refresh();
            log.info("Resource auto-load completed successfully");
        } catch (Exception e) {
            log.error("Resource auto-load failed: {}", e.getMessage(), e);
            if (failOnError) {
                throw new RuntimeException("Resource auto-load failed", e);
            }
            resourceStore.refresh();
        }
    }

    private void loadResourcesFromControllers() {
        Map<String, Object> controllers = applicationContext.getBeansWithAnnotation(RestController.class);

        for (Map.Entry<String, Object> entry : controllers.entrySet()) {
            try {
                processController(entry.getValue());
            } catch (Exception e) {
                log.warn("Failed to process controller {}: {}", entry.getKey(), e.getMessage());
            }
        }
    }

    private void processController(Object controller) {
        Class<?> controllerClass = controller.getClass();
        if (controllerClass.getName().contains("$$")) {
            controllerClass = controllerClass.getSuperclass();
        }

        AuthResource classAnnotation = controllerClass.getAnnotation(AuthResource.class);
        String baseMapping = extractBaseMapping(controllerClass);

        for (Method method : controllerClass.getDeclaredMethods()) {
            AuthResource methodAnnotation = method.getAnnotation(AuthResource.class);
            if (methodAnnotation == null) continue;

            String httpMethod = extractHttpMethod(method);
            String endpoint = baseMapping + extractMethodMapping(method);
            String resourceId = buildResourceId(controllerClass.getSimpleName(), method.getName());
            String category = !methodAnnotation.category().isBlank()
                    ? methodAnnotation.category()
                    : (classAnnotation != null && !classAnnotation.category().isBlank()
                    ? classAnnotation.category() : defaultCategory);

            upsertResource(resourceId, methodAnnotation, controllerClass, method,
                    httpMethod, endpoint, category);
        }
    }

    private void upsertResource(String resourceId, AuthResource annotation, Class<?> controller,
                                 Method method, String httpMethod, String endpoint, String category) {
        Resource resource = resourceRepository.findById(resourceId).orElseGet(() -> {
            Resource r = new Resource();
            r.setResourceId(resourceId);
            return r;
        });

        boolean isNew = resource.getCreatedAt() == null;

        if (isNew || updateExisting) {
            resource.setResourceName(annotation.value());
            resource.setResourceDescription(annotation.description());
            resource.setResourceCategory(category);
            resource.setControllerClass(controller.getSimpleName());
            resource.setMethodName(method.getName());
            resource.setHttpMethod(httpMethod);
            resource.setBaseEndpoint(endpoint);
            resource.setPattern(endpoint.contains("{") ? endpoint : null);
            resource.setIsPublicResource(annotation.isPublic());
            resource.setRequiresOwnership(annotation.requiresOwnership());
            resource.setIsCoreResource(annotation.isCoreResource());
            resource.setIsActive(true);

            resourceRepository.save(resource);
            log.debug("Resource {}: {} {} [{}]", isNew ? "created" : "updated",
                    httpMethod, endpoint, resourceId);
        }
    }

    private String extractBaseMapping(Class<?> controllerClass) {
        RequestMapping rm = controllerClass.getAnnotation(RequestMapping.class);
        if (rm != null && rm.value().length > 0) return rm.value()[0];
        return "";
    }

    private String extractMethodMapping(Method method) {
        if (method.isAnnotationPresent(GetMapping.class)) {
            GetMapping m = method.getAnnotation(GetMapping.class);
            return m.value().length > 0 ? m.value()[0] : "";
        }
        if (method.isAnnotationPresent(PostMapping.class)) {
            PostMapping m = method.getAnnotation(PostMapping.class);
            return m.value().length > 0 ? m.value()[0] : "";
        }
        if (method.isAnnotationPresent(PutMapping.class)) {
            PutMapping m = method.getAnnotation(PutMapping.class);
            return m.value().length > 0 ? m.value()[0] : "";
        }
        if (method.isAnnotationPresent(DeleteMapping.class)) {
            DeleteMapping m = method.getAnnotation(DeleteMapping.class);
            return m.value().length > 0 ? m.value()[0] : "";
        }
        if (method.isAnnotationPresent(PatchMapping.class)) {
            PatchMapping m = method.getAnnotation(PatchMapping.class);
            return m.value().length > 0 ? m.value()[0] : "";
        }
        if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping m = method.getAnnotation(RequestMapping.class);
            return m.value().length > 0 ? m.value()[0] : "";
        }
        return "";
    }

    private String extractHttpMethod(Method method) {
        if (method.isAnnotationPresent(GetMapping.class)) return "GET";
        if (method.isAnnotationPresent(PostMapping.class)) return "POST";
        if (method.isAnnotationPresent(PutMapping.class)) return "PUT";
        if (method.isAnnotationPresent(DeleteMapping.class)) return "DELETE";
        if (method.isAnnotationPresent(PatchMapping.class)) return "PATCH";
        if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMethod[] methods = method.getAnnotation(RequestMapping.class).method();
            if (methods.length > 0) return methods[0].name();
        }
        return "GET";
    }

    private String buildResourceId(String controllerName, String methodName) {
        return (controllerName.replace("Controller", "").toLowerCase() + "-" + methodName)
                .replaceAll("([A-Z])", "-$1").toLowerCase()
                .replaceAll("-+", "-")
                .replaceAll("^-", "");
    }
}

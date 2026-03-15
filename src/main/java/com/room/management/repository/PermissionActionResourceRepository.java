package com.room.management.repository;

import com.room.management.entity.auth.PermissionActionResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionActionResourceRepository extends JpaRepository<PermissionActionResource, Long> {

    @Query("""
            SELECT par FROM PermissionActionResource par
            JOIN FETCH par.permission p
            JOIN FETCH par.action a
            JOIN FETCH par.resource r
            WHERE par.isActive = true
            """)
    List<PermissionActionResource> findAllActiveWithDetails();

    @Query("""
            SELECT par FROM PermissionActionResource par
            JOIN FETCH par.permission p
            JOIN FETCH par.action a
            JOIN FETCH par.resource r
            WHERE par.isActive = true AND r.resourceId = :resourceId
            """)
    List<PermissionActionResource> findActiveByResourceId(@Param("resourceId") String resourceId);

    @Query("""
            SELECT par FROM PermissionActionResource par
            JOIN FETCH par.permission p
            JOIN FETCH par.action a
            JOIN FETCH par.resource r
            WHERE par.isActive = true AND p.permissionCode = :permissionCode
            """)
    List<PermissionActionResource> findActiveByPermissionCode(@Param("permissionCode") String permissionCode);

    @Query("SELECT COUNT(par) > 0 FROM PermissionActionResource par WHERE par.permission.permissionCode = :permissionCode AND par.action.actionCode = :actionCode AND par.resource.resourceId = :resourceId")
    boolean existsByPermissionCodeAndActionCodeAndResourceId(
            @Param("permissionCode") String permissionCode,
            @Param("actionCode") String actionCode,
            @Param("resourceId") String resourceId);
}

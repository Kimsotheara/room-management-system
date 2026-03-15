package com.room.management.repository;

import com.room.management.entity.auth.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, String> {

    Optional<Resource> findByResourceNameAndIsActiveTrue(String resourceName);

    List<Resource> findAllByIsActiveTrue();

    @Query("SELECT r FROM Resource r WHERE r.isActive = true AND r.isPublicResource = true")
    List<Resource> findAllPublicResources();

    @Query("""
            SELECT r FROM Resource r
            WHERE r.isActive = true
              AND r.httpMethod = :httpMethod
              AND (r.baseEndpoint = :endpoint OR r.pattern IS NOT NULL)
            """)
    List<Resource> findByHttpMethodAndEndpoint(@Param("httpMethod") String httpMethod,
                                                @Param("endpoint") String endpoint);

    @Query("SELECT r FROM Resource r WHERE r.resourceCategory = :category AND r.isActive = true")
    List<Resource> findByCategory(@Param("category") String category);

    boolean existsByResourceName(String resourceName);
}

package com.room.management.repository;

import com.room.management.entity.auth.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {

    List<Permission> findAllByIsActiveTrue();

    @Query("SELECT p FROM Permission p WHERE p.isActive = true AND p.parentPermission IS NULL")
    List<Permission> findAllRootPermissions();
}

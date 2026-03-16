package com.room.management.repository;

import com.room.management.entity.room.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicesRepository extends JpaRepository<Services, Long> {
    boolean existsByName(String name);
}

package com.room.management.repository;

import com.room.management.entity.room.Promotions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotions, Long> {
    boolean existsByName(String name);
}

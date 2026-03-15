package com.room.management.repository;

import com.room.management.entity.auth.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionRepository extends JpaRepository<Action, String> {

    List<Action> findAllByIsActiveTrue();
}

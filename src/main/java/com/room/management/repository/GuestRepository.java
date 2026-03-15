package com.room.management.repository;

import com.room.management.entity.room.Guests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRepository extends JpaRepository<Guests, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByIdentityNumber(String identityNumber);

    boolean existsByIdentityNumberAndIdNot(String identityNumber, Long id);
}

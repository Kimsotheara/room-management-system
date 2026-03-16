package com.room.management.repository;

import com.room.management.dto.request.GuestRequestDto;
import com.room.management.dto.request.PageAbleRequest;
import com.room.management.entity.room.Guests;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Repository
public interface GuestRepository extends JpaRepository<Guests, Long> {

    @Query("SELECT g.profileImage FROM Guests g WHERE g.id = :id")
    Optional<byte[]> findProfileImageById(@Param("id") Long id);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByIdentityNumber(String identityNumber);

    boolean existsByIdentityNumberAndIdNot(String identityNumber, Long id);

    @Query("""
            SELECT g FROM Guests g
            WHERE (:firstName = '' OR LOWER(g.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')))
              AND (:lastName = '' OR LOWER(g.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')))
              AND (:email = '' OR LOWER(g.email) LIKE LOWER(CONCAT('%', :email, '%')))
              AND (:phoneNumber = '' OR g.phoneNumber LIKE CONCAT('%', :phoneNumber, '%'))
              AND (:nationality = '' OR LOWER(g.nationality) LIKE LOWER(CONCAT('%', :nationality, '%')))
              AND (:identityType = '' OR LOWER(g.identityType) LIKE LOWER(CONCAT('%', :identityType, '%')))
              AND (cast(:isActive as Boolean) IS NULL OR g.isActive = :isActive)
            """)
    Page<Guests> executeFilterQuery(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber,
            @Param("nationality") String nationality,
            @Param("identityType") String identityType,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

    default Page<Guests> findAllWithFilter(PageAbleRequest<GuestRequestDto> request) {
        GuestRequestDto f = request.getParameter();
        return executeFilterQuery(
                f != null && StringUtils.hasText(f.getFirstName())    ? f.getFirstName()    : "",
                f != null && StringUtils.hasText(f.getLastName())     ? f.getLastName()     : "",
                f != null && StringUtils.hasText(f.getEmail())        ? f.getEmail()        : "",
                f != null && StringUtils.hasText(f.getPhoneNumber())  ? f.getPhoneNumber()  : "",
                f != null && StringUtils.hasText(f.getNationality())  ? f.getNationality()  : "",
                f != null && StringUtils.hasText(f.getIdentityType()) ? f.getIdentityType() : "",
                f != null ? f.getIsActive() : null,
                request.getPageAble()
        );
    }
}

package com.tpa.repository;

import com.tpa.entity.Carrier;
import com.tpa.entity.User;
import com.tpa.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface CarrierRepository extends JpaRepository<Carrier, Long> {

    Optional<Carrier> findByCompanyNameIgnoreCase(String companyName);

    Optional<Carrier> findByUser_Email(String email);

    Optional<Carrier> findByUser_Username(String username);

    Optional<Carrier> findByUser_Id(Long userId);

    boolean existsByRegistrationNumber(String registrationNumber);

    boolean existsByUser(User user);

    Page<Carrier> findByCompanyNameContainingIgnoreCaseAndUser_UserStatus(String companyName, UserStatus userStatus, Pageable pageable);

    Page<Carrier> findByCompanyNameContainingIgnoreCaseAndUser_UserStatusIn(String companyName, Collection<UserStatus> userStatuses, Pageable pageable);

    Page<Carrier> findByCompanyNameContainingIgnoreCase(String companyName, Pageable pageable);

    Page<Carrier> findByUser_UserStatus(UserStatus userStatus, Pageable pageable);

    Page<Carrier> findByUser_UserStatusIn(Collection<UserStatus> userStatuses, Pageable pageable);
}

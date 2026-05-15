package com.tpa.repository;

import com.tpa.entity.Carrier;
import com.tpa.entity.User;
import com.tpa.enums.UserRole;
import com.tpa.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String adminEmail);

    Optional<User> findByEmail(String username);

    boolean existsByEmailAndPhoneNumber(String email, String phoneNumber);

    Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email, Pageable pageable);

    List<User> findByUserRole(UserRole role);

    Page<User> findByUserRole(UserRole userRole, Pageable pageable);

    Page<User> findByUsernameContainingIgnoreCaseAndUserRole(String username, UserRole userRole, Pageable pageable);

    Page<User> findByEmailContainingIgnoreCaseAndUserRole(String email, UserRole userRole, Pageable pageable);

    Page<User> findByUserStatusAndUserRole(UserStatus userStatus, UserRole userRole, Pageable pageable);

    Page<User> findByUsernameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatusAndUserRole(String username, String email, UserStatus userStatus, UserRole userRole, Pageable pageable);
}

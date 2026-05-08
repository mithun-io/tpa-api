package com.tpa.repository;

import com.tpa.entity.User;
import com.tpa.enums.UserRole;
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

    boolean existsByEmailAndMobile(String email, String mobile);

    Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email, Pageable pageable);

    List<User> findByUserRole(UserRole role);
}

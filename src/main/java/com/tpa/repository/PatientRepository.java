package com.tpa.repository;

import com.tpa.entity.Patient;
import com.tpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    boolean existsByUser(User user);
}

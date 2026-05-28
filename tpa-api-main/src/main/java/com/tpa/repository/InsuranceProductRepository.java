package com.tpa.repository;

import com.tpa.entity.InsuranceProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsuranceProductRepository extends JpaRepository<InsuranceProduct, Long> {
}

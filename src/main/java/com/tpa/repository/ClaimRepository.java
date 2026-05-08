package com.tpa.repository;

import com.tpa.entity.Claim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long>, JpaSpecificationExecutor<Claim> {

    @EntityGraph(attributePaths = {"user", "carrier"})
    List<Claim> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"user", "carrier"})
    List<Claim> findByCarrier_Id(Long carrierId);

    boolean existsByPolicyNumber(String policyNumber);

    @Query("SELECT c.status, COUNT(c) FROM Claim c GROUP BY c.status")
    List<Object[]> countClaimsByStatus();

    @Query("SELECT CAST(c.createdDate AS date), COUNT(c) FROM Claim c WHERE c.createdDate >= :startDate GROUP BY CAST(c.createdDate AS date) ORDER BY CAST(c.createdDate AS date)")
    List<Object[]> countClaimsPerDay(@org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate);

    @Query("SELECT SUM(c.amount) FROM Claim c WHERE c.status = com.tpa.enums.ClaimStatus.CARRIER_APPROVED OR c.status = com.tpa.enums.ClaimStatus.ADMIN_APPROVED OR c.status = com.tpa.enums.ClaimStatus.SETTLED")
    Double sumApprovedClaimAmount();

    boolean existsByBillNumberAndIdNot(String billNumber, Long id);

    @Override
    @EntityGraph(attributePaths = {"user", "carrier"})
    Page<Claim> findAll(Specification<Claim> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"user", "carrier"})
    List<Claim> findAll();
}

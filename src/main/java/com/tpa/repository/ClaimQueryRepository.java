package com.tpa.repository;

import com.tpa.entity.ClaimQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimQueryRepository extends JpaRepository<ClaimQuery, Long> {

    List<ClaimQuery> findByClaimIdOrderByTimestampAsc(Long claimId);
}

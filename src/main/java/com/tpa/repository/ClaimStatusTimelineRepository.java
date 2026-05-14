package com.tpa.repository;

import com.tpa.entity.ClaimStatusTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimStatusTimelineRepository extends JpaRepository<ClaimStatusTimeline, Long> {

    List<ClaimStatusTimeline> findByClaimIdOrderByOccurredAtAsc(Long claimId);
}

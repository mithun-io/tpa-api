package com.tpa.controller;

import com.tpa.entity.Claim;
import com.tpa.entity.ClaimQuery;
import com.tpa.repository.ClaimQueryRepository;
import com.tpa.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/queries")
@RequiredArgsConstructor
public class ClaimQueryController {

    private final ClaimQueryRepository queryRepository;
    private final ClaimRepository claimRepository;

    @GetMapping("/{claimId}")
    public ResponseEntity<List<ClaimQuery>> getQueries(@PathVariable Long claimId) {
        return ResponseEntity.ok(queryRepository.findByClaimIdOrderByTimestampAsc(claimId));
    }

    @PostMapping("/{claimId}")
    public ResponseEntity<ClaimQuery> postQuery(
            @PathVariable Long claimId,
            @RequestBody ClaimQuery queryRequest,
            @RequestParam String username,
            @RequestParam boolean isCarrier) {
        
        Claim claim = claimRepository.findById(claimId).orElseThrow();
        
        ClaimQuery query = new ClaimQuery(claim, username, queryRequest.getMessage(), isCarrier);
        
        return ResponseEntity.ok(queryRepository.save(query));
    }
}

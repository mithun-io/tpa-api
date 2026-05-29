package com.tpa.service;

import com.tpa.dto.request.claim.ClaimReviewRequest;
import com.tpa.dto.response.analytics.MonitoringResponse;
import com.tpa.dto.response.claim.ClaimResponse;
import com.tpa.dto.response.user.CarrierResponse;
import com.tpa.dto.response.user.UserResponse;
import com.tpa.enums.ClaimStatus;
import com.tpa.enums.UserStatus;
import org.springframework.data.domain.Page;

import java.security.Principal;
import java.time.LocalDateTime;

public interface AdminService {

    UserResponse blockUser(Long id);

    UserResponse unblockUser(Long id);

    Page<UserResponse> getAllUsers(int page, int size, String search);

    Page<CarrierResponse> getAllCarriers(String companyName, UserStatus userStatus, int page, int size, String sortBy, boolean desc);

    Page<UserResponse> getAllPatients(String username, String email, UserStatus userStatus, int page, int size, String sortBy, boolean desc);

    Page<ClaimResponse> getAllClaims(ClaimStatus claimStatus, LocalDateTime createdAt, int page, int size, String sortBy, boolean desc);

    ClaimResponse reviewClaim(ClaimReviewRequest claimReviewRequest, Principal principal);

    ClaimResponse approveClaim(Long claimId, String reason, Principal principal);

    ClaimResponse rejectClaim(Long claimId, String reason, Principal principal);

    CarrierResponse approveCarrier(Long carrierId);

    CarrierResponse rejectCarrier(Long carrierId);

    ClaimResponse assignClaimToCarrier(Long claimId, Long carrierId);

    MonitoringResponse getSystemMonitoring();
}
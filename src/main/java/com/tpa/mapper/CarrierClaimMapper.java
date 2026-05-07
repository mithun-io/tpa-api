package com.tpa.mapper;

import com.tpa.dto.response.CarrierClaimDetailResponse;
import com.tpa.dto.response.CarrierClaimDetailResponse.FraudInfo;
import com.tpa.dto.response.CarrierClaimDetailResponse.PatientInfo;
import com.tpa.dto.response.CarrierClaimDetailResponse.PolicyInfo;
import com.tpa.entity.Claim;
import com.tpa.entity.User;
import com.tpa.enums.ClaimStatus;
import com.tpa.enums.PolicyStatus;
import com.tpa.enums.RiskLevel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarrierClaimMapper {

    @Mapping(source = "id", target = "claimId")
    @Mapping(source = "claim", target = "patient", qualifiedByName = "mapPatientInfo")
    @Mapping(source = "claim", target = "fraud", qualifiedByName = "mapFraudInfo")
    @Mapping(source = "claim", target = "policy", qualifiedByName = "mapPolicyInfo")
    CarrierClaimDetailResponse toCarrierClaimDetailResponse(Claim claim);

    List<CarrierClaimDetailResponse> toCarrierClaimDetailResponses(List<Claim> claims);

    @Named("mapPatientInfo")
    default PatientInfo mapPatientInfo(Claim c) {
        User u = c.getUser();
        if (u == null) return null;
        return PatientInfo.builder()
                .name(u.getUsername())
                .email(u.getEmail())
                .mobile(u.getMobile())
                .dateOfBirth(u.getDateOfBirth())
                .gender(u.getGender() != null ? u.getGender().name() : null)
                .address(u.getAddress())
                .build();
    }

    @Named("mapFraudInfo")
    default FraudInfo mapFraudInfo(Claim c) {
        RiskLevel mappedRiskLevel = RiskLevel.LOW;
        if (c.getRiskLevel() != null) {
            try {
                mappedRiskLevel = RiskLevel.valueOf(c.getRiskLevel().name());
            } catch (IllegalArgumentException e) {
                mappedRiskLevel = RiskLevel.LOW;
            }
        }
        return FraudInfo.builder()
                .riskScore(c.getRiskScore())
                .riskLevel(mappedRiskLevel)
                .healthScore(c.getHealthScore())
                .riskFlags(c.getRiskFlags())
                .aiSummary(c.getAiSummary())
                .build();
    }

    @Named("mapPolicyInfo")
    default PolicyInfo mapPolicyInfo(Claim c) {
        boolean hasPolicy = c.getPolicyNumber() != null && !c.getPolicyNumber().isBlank() && !c.getPolicyNumber().startsWith("TEMP-");
        boolean hasAmount = c.getAmount() != null && c.getAmount() > 0;
        boolean notRejected = c.getStatus() != ClaimStatus.REJECTED;

        String polStatus = (hasPolicy && hasAmount && notRejected) ? "VALID" : "INVALID";
        String polReason = "VALID".equals(polStatus)
                ? "Policy is active and claim details are complete."
                : !hasPolicy ? "Missing or temporary policy number."
                  : !hasAmount ? "Claim amount is zero or missing."
                    : "Claim has been rejected — policy coverage cannot be applied.";

        return PolicyInfo.builder()
                .policyNumber(c.getPolicyNumber())
                .policyStatus(PolicyStatus.valueOf(polStatus))
                .reason(polReason)
                .build();
    }
}

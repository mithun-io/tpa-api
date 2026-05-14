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
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarrierClaimMapper {

    @Mapping(source = "id", target = "claimId")
    @Mapping(target = "patient", expression = "java(mapPatientInfo(claim))")
    @Mapping(target = "fraud", expression = "java(mapFraudInfo(claim))")
    @Mapping(target = "policy", expression = "java(mapPolicyInfo(claim))")
    CarrierClaimDetailResponse toCarrierClaimDetailResponse(Claim claim);

    List<CarrierClaimDetailResponse> toCarrierClaimDetailResponses(List<Claim> claims);

    @Named("mapPatientInfo")
    default PatientInfo mapPatientInfo(Claim c) {
        User u = c.getUser();
        if (u == null) return null;
        return PatientInfo.builder()
                .name(u.getUsername())
                .email(u.getEmail())
                .mobile(u.getPhoneNumber())
                .dateOfBirth(u.getDateOfBirth())
                .gender(u.getGender() != null ? u.getGender().name() : null)
                .address(u.getAddress())
                .build();
    }

    @Named("mapFraudInfo")
    default FraudInfo mapFraudInfo(Claim c) {
        RiskLevel mappedRiskLevel = RiskLevel.LOW;
        if (c.getRiskLevel() != null) {
            mappedRiskLevel = c.getRiskLevel();
        }
        Double hScore = c.getHealthScore();
        return FraudInfo.builder()
                .riskScore(c.getRiskScore())
                .riskLevel(mappedRiskLevel)
                .healthScore(hScore != null ? hScore.intValue() : null)
                .riskFlags(c.getRiskFlags())
                .aiSummary(c.getAiSummary())
                .build();
    }

    @Named("mapPolicyInfo")
    default PolicyInfo mapPolicyInfo(Claim c) {
        boolean hasPolicy = c.getPolicyNumber() != null && !c.getPolicyNumber().isBlank() && !c.getPolicyNumber().startsWith("TEMP-");
        boolean hasAmount = c.getAmount() != null && c.getAmount() > 0;
        boolean notRejected = c.getClaimStatus() != ClaimStatus.REJECTED;

        String polStatus = (hasPolicy && hasAmount && notRejected) ? "VALID" : "INVALID";
        String polReason = "VALID".equals(polStatus)
                ? "Policy is active and claim details are complete."
                : !hasPolicy
                  ? "Missing or temporary policy number."
                  : !hasAmount
                    ? "Claim amount is zero or missing."
                    : "Claim has been rejected \u2014 policy coverage cannot be applied.";

        return PolicyInfo.builder()
                .policyNumber(c.getPolicyNumber())
                .policyStatus(PolicyStatus.valueOf(polStatus))
                .reason(polReason)
                .build();
    }
}

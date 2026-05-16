package com.tpa.mapper;

import com.tpa.dto.response.claim.CarrierClaimDetailResponse;
import com.tpa.dto.response.claim.CarrierClaimDetailResponse.FraudInfo;
import com.tpa.dto.response.claim.CarrierClaimDetailResponse.PatientInfo;
import com.tpa.entity.Claim;
import com.tpa.entity.User;
import com.tpa.enums.RiskLevel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = PolicyValidationMapper.class)
public interface CarrierClaimMapper {

    @Mapping(source = "id", target = "claimId")
    @Mapping(target = "patientInfo", expression = "java(mapPatientInfo(claim))")
    @Mapping(target = "fraudInfo", expression = "java(mapFraudInfo(claim))")
    @Mapping(source = "claim", target = "policyInfo")
    CarrierClaimDetailResponse toCarrierClaimDetailResponse(Claim claim);

    List<CarrierClaimDetailResponse> toCarrierClaimDetailResponses(List<Claim> claims);

    @Named("mapPatientInfo")
    default PatientInfo mapPatientInfo(Claim claim) {

        User user = claim.getUser();

        if (user == null) {
            return null;
        }

        return PatientInfo.builder()
                .name(user.getUsername())
                .email(user.getEmail())
                .mobile(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender() != null ? user.getGender().name() : null)
                .address(user.getAddress())
                .build();
    }

    @Named("mapFraudInfo")
    default FraudInfo mapFraudInfo(Claim claim) {
        RiskLevel riskLevel = claim.getRiskLevel() != null ? claim.getRiskLevel() : RiskLevel.LOW;
        Double healthScore = claim.getHealthScore();

        return FraudInfo.builder()
                .riskScore(claim.getRiskScore())
                .riskLevel(riskLevel)
                .healthScore(healthScore != null ? healthScore.intValue() : null)
                .riskFlags(claim.getRiskFlags())
                .aiSummary(claim.getAiSummary())
                .build();
    }
}
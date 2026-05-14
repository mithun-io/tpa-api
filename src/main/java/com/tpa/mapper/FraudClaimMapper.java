package com.tpa.mapper;

import com.tpa.dto.response.FraudClaimResponse;
import com.tpa.entity.Claim;
import com.tpa.enums.RiskLevel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FraudClaimMapper {

    @Mapping(source = "id", target = "claimId")
    @Mapping(source = "claim", target = "reasons", qualifiedByName = "mapReasons")
    @Mapping(source = "claim", target = "riskLevel", qualifiedByName = "mapRiskLevel")
    FraudClaimResponse toFraudClaimDto(Claim claim);

    @Named("mapReasons")
    default List<String> mapReasons(Claim c) {
        if (c.getRiskFlags() != null && !c.getRiskFlags().isBlank()) {
            return List.of(c.getRiskFlags().split(", "));
        }
        return List.of();
    }

    @Named("mapRiskLevel")
    default RiskLevel mapRiskLevel(Claim c) {
        String level = c.getRiskLevel() != null ? c.getRiskLevel().name() : "LOW";
        try {
            return RiskLevel.valueOf(level);
        } catch (IllegalArgumentException e) {
            return RiskLevel.LOW;
        }
    }
}

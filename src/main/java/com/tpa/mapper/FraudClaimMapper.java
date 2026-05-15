package com.tpa.mapper;

import com.tpa.dto.response.claim.FraudClaimResponse;
import com.tpa.entity.Claim;
import com.tpa.enums.RiskLevel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FraudClaimMapper {

    @Mapping(source = "id", target = "claimId")
    @Mapping(source = ".", target = "reasons", qualifiedByName = "mapReasons")
    @Mapping(source = ".", target = "riskLevel", qualifiedByName = "mapRiskLevel")
    FraudClaimResponse toFraudClaimDto(Claim claim);

    List<FraudClaimResponse> toFraudClaimDtos(List<Claim> claims);

    @Named("mapReasons")
    default List<String> mapReasons(Claim c) {
        if (c.getRiskFlags() != null && !c.getRiskFlags().isBlank()) {
            return List.of(c.getRiskFlags().split(", "));
        }
        return List.of();
    }

    @Named("mapRiskLevel")
    default RiskLevel mapRiskLevel(Claim c) {
        if (c.getRiskLevel() != null) {
            return c.getRiskLevel();
        }
        return RiskLevel.LOW;
    }
}

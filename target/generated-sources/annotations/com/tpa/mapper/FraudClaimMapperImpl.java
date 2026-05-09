package com.tpa.mapper;

import com.tpa.dto.response.FraudClaimDto;
import com.tpa.entity.Claim;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-08T19:44:21+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class FraudClaimMapperImpl implements FraudClaimMapper {

    @Override
    public FraudClaimDto toFraudClaimDto(Claim claim) {
        if ( claim == null ) {
            return null;
        }

        FraudClaimDto.FraudClaimDtoBuilder fraudClaimDto = FraudClaimDto.builder();

        fraudClaimDto.claimId( claim.getId() );
        fraudClaimDto.reasons( mapReasons( claim ) );
        fraudClaimDto.riskLevel( mapRiskLevel( claim ) );
        fraudClaimDto.amount( claim.getAmount() );
        fraudClaimDto.policyNumber( claim.getPolicyNumber() );
        fraudClaimDto.riskScore( claim.getRiskScore() );

        return fraudClaimDto.build();
    }
}

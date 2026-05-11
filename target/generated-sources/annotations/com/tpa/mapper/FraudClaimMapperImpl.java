package com.tpa.mapper;

import com.tpa.dto.response.FraudClaimDto;
import com.tpa.entity.Claim;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-11T12:55:18+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
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
        fraudClaimDto.policyNumber( claim.getPolicyNumber() );
        fraudClaimDto.amount( claim.getAmount() );
        fraudClaimDto.riskScore( claim.getRiskScore() );

        return fraudClaimDto.build();
    }
}

package com.tpa.mapper;

import com.tpa.dto.response.claim.FraudClaimResponse;
import com.tpa.entity.Claim;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-17T09:13:19+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class FraudClaimMapperImpl implements FraudClaimMapper {

    @Override
    public FraudClaimResponse toFraudClaimDto(Claim claim) {
        if ( claim == null ) {
            return null;
        }

        FraudClaimResponse.FraudClaimResponseBuilder fraudClaimResponse = FraudClaimResponse.builder();

        fraudClaimResponse.claimId( claim.getId() );
        fraudClaimResponse.reasons( mapReasons( claim ) );
        fraudClaimResponse.riskLevel( mapRiskLevel( claim ) );
        fraudClaimResponse.amount( claim.getAmount() );
        fraudClaimResponse.policyNumber( claim.getPolicyNumber() );
        fraudClaimResponse.riskScore( claim.getRiskScore() );

        return fraudClaimResponse.build();
    }

    @Override
    public List<FraudClaimResponse> toFraudClaimDtos(List<Claim> claims) {
        if ( claims == null ) {
            return null;
        }

        List<FraudClaimResponse> list = new ArrayList<FraudClaimResponse>( claims.size() );
        for ( Claim claim : claims ) {
            list.add( toFraudClaimDto( claim ) );
        }

        return list;
    }
}

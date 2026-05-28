package com.tpa.mapper;

import com.tpa.dto.response.claim.FraudClaimResponse;
import com.tpa.entity.Claim;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-28T10:48:18+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
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
        fraudClaimResponse.policyNumber( claim.getPolicyNumber() );
        fraudClaimResponse.amount( claim.getAmount() );
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

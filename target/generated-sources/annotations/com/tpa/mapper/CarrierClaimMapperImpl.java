package com.tpa.mapper;

import com.tpa.dto.response.CarrierClaimDetailResponse;
import com.tpa.entity.Claim;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-11T12:55:18+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class CarrierClaimMapperImpl implements CarrierClaimMapper {

    @Override
    public CarrierClaimDetailResponse toCarrierClaimDetailResponse(Claim claim) {
        if ( claim == null ) {
            return null;
        }

        CarrierClaimDetailResponse.CarrierClaimDetailResponseBuilder carrierClaimDetailResponse = CarrierClaimDetailResponse.builder();

        carrierClaimDetailResponse.claimId( claim.getId() );
        carrierClaimDetailResponse.patient( mapPatientInfo( claim ) );
        carrierClaimDetailResponse.fraud( mapFraudInfo( claim ) );
        carrierClaimDetailResponse.policy( mapPolicyInfo( claim ) );
        carrierClaimDetailResponse.policyNumber( claim.getPolicyNumber() );
        if ( claim.getStatus() != null ) {
            carrierClaimDetailResponse.status( claim.getStatus().name() );
        }
        carrierClaimDetailResponse.amount( claim.getAmount() );
        carrierClaimDetailResponse.totalBillAmount( claim.getTotalBillAmount() );
        carrierClaimDetailResponse.claimType( claim.getClaimType() );
        carrierClaimDetailResponse.diagnosis( claim.getDiagnosis() );
        carrierClaimDetailResponse.hospitalName( claim.getHospitalName() );
        carrierClaimDetailResponse.admissionDate( claim.getAdmissionDate() );
        carrierClaimDetailResponse.dischargeDate( claim.getDischargeDate() );
        carrierClaimDetailResponse.createdDate( claim.getCreatedDate() );
        carrierClaimDetailResponse.processedDate( claim.getProcessedDate() );
        carrierClaimDetailResponse.rejectionReason( claim.getRejectionReason() );
        carrierClaimDetailResponse.reviewNotes( claim.getReviewNotes() );
        carrierClaimDetailResponse.reviewedBy( claim.getReviewedBy() );
        carrierClaimDetailResponse.reviewedAt( claim.getReviewedAt() );

        return carrierClaimDetailResponse.build();
    }

    @Override
    public List<CarrierClaimDetailResponse> toCarrierClaimDetailResponses(List<Claim> claims) {
        if ( claims == null ) {
            return null;
        }

        List<CarrierClaimDetailResponse> list = new ArrayList<CarrierClaimDetailResponse>( claims.size() );
        for ( Claim claim : claims ) {
            list.add( toCarrierClaimDetailResponse( claim ) );
        }

        return list;
    }
}

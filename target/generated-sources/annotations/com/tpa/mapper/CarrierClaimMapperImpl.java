package com.tpa.mapper;

import com.tpa.dto.response.claim.CarrierClaimDetailResponse;
import com.tpa.entity.Claim;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-29T16:27:11+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class CarrierClaimMapperImpl implements CarrierClaimMapper {

    @Autowired
    private PolicyValidationMapper policyValidationMapper;

    @Override
    public CarrierClaimDetailResponse toCarrierClaimDetailResponse(Claim claim) {
        if ( claim == null ) {
            return null;
        }

        CarrierClaimDetailResponse.CarrierClaimDetailResponseBuilder carrierClaimDetailResponse = CarrierClaimDetailResponse.builder();

        carrierClaimDetailResponse.claimId( claim.getId() );
        carrierClaimDetailResponse.policyInfo( policyValidationMapper.mapPolicyInfo( claim ) );
        carrierClaimDetailResponse.policyNumber( claim.getPolicyNumber() );
        carrierClaimDetailResponse.claimType( claim.getClaimType() );
        carrierClaimDetailResponse.claimStatus( claim.getClaimStatus() );
        carrierClaimDetailResponse.amount( claim.getAmount() );
        carrierClaimDetailResponse.totalBillAmount( claim.getTotalBillAmount() );
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

        carrierClaimDetailResponse.patientInfo( mapPatientInfo(claim) );
        carrierClaimDetailResponse.fraudInfo( mapFraudInfo(claim) );

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

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
    date = "2026-05-17T09:13:20+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
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
        carrierClaimDetailResponse.admissionDate( claim.getAdmissionDate() );
        carrierClaimDetailResponse.amount( claim.getAmount() );
        carrierClaimDetailResponse.claimStatus( claim.getClaimStatus() );
        carrierClaimDetailResponse.claimType( claim.getClaimType() );
        carrierClaimDetailResponse.createdDate( claim.getCreatedDate() );
        carrierClaimDetailResponse.diagnosis( claim.getDiagnosis() );
        carrierClaimDetailResponse.dischargeDate( claim.getDischargeDate() );
        carrierClaimDetailResponse.hospitalName( claim.getHospitalName() );
        carrierClaimDetailResponse.policyNumber( claim.getPolicyNumber() );
        carrierClaimDetailResponse.processedDate( claim.getProcessedDate() );
        carrierClaimDetailResponse.rejectionReason( claim.getRejectionReason() );
        carrierClaimDetailResponse.reviewNotes( claim.getReviewNotes() );
        carrierClaimDetailResponse.reviewedAt( claim.getReviewedAt() );
        carrierClaimDetailResponse.reviewedBy( claim.getReviewedBy() );
        carrierClaimDetailResponse.totalBillAmount( claim.getTotalBillAmount() );

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

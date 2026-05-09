package com.tpa.mapper;

import com.tpa.dto.response.ClaimResponse;
import com.tpa.entity.Claim;
import com.tpa.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-08T19:44:21+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class ClaimMapperImpl implements ClaimMapper {

    @Override
    public ClaimResponse toClaimResponse(Claim claim) {
        if ( claim == null ) {
            return null;
        }

        ClaimResponse.ClaimResponseBuilder claimResponse = ClaimResponse.builder();

        claimResponse.username( claimUserUsername( claim ) );
        claimResponse.userEmail( claimUserEmail( claim ) );
        claimResponse.claimStatus( claim.getStatus() );
        claimResponse.admissionDate( claim.getAdmissionDate() );
        claimResponse.aiSummary( claim.getAiSummary() );
        claimResponse.amount( claim.getAmount() );
        claimResponse.claimType( claim.getClaimType() );
        claimResponse.createdDate( claim.getCreatedDate() );
        claimResponse.diagnosis( claim.getDiagnosis() );
        claimResponse.dischargeDate( claim.getDischargeDate() );
        claimResponse.healthScore( claim.getHealthScore() );
        claimResponse.hospitalName( claim.getHospitalName() );
        claimResponse.id( claim.getId() );
        claimResponse.patientName( claim.getPatientName() );
        claimResponse.policyNumber( claim.getPolicyNumber() );
        claimResponse.processedDate( claim.getProcessedDate() );
        claimResponse.rejectionReason( claim.getRejectionReason() );
        claimResponse.reviewNotes( claim.getReviewNotes() );
        claimResponse.reviewedAt( claim.getReviewedAt() );
        claimResponse.reviewedBy( claim.getReviewedBy() );
        claimResponse.riskFlags( claim.getRiskFlags() );
        if ( claim.getRiskLevel() != null ) {
            claimResponse.riskLevel( claim.getRiskLevel().name() );
        }
        claimResponse.riskScore( claim.getRiskScore() );
        claimResponse.totalBillAmount( claim.getTotalBillAmount() );

        return claimResponse.build();
    }

    @Override
    public List<ClaimResponse> toClaimResponses(List<Claim> claims) {
        if ( claims == null ) {
            return null;
        }

        List<ClaimResponse> list = new ArrayList<ClaimResponse>( claims.size() );
        for ( Claim claim : claims ) {
            list.add( toClaimResponse( claim ) );
        }

        return list;
    }

    private String claimUserUsername(Claim claim) {
        if ( claim == null ) {
            return null;
        }
        User user = claim.getUser();
        if ( user == null ) {
            return null;
        }
        String username = user.getUsername();
        if ( username == null ) {
            return null;
        }
        return username;
    }

    private String claimUserEmail(Claim claim) {
        if ( claim == null ) {
            return null;
        }
        User user = claim.getUser();
        if ( user == null ) {
            return null;
        }
        String email = user.getEmail();
        if ( email == null ) {
            return null;
        }
        return email;
    }
}

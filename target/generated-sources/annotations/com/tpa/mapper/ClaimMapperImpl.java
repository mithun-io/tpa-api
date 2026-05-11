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
    date = "2026-05-11T12:55:18+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
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
        claimResponse.id( claim.getId() );
        claimResponse.policyNumber( claim.getPolicyNumber() );
        claimResponse.amount( claim.getAmount() );
        claimResponse.createdDate( claim.getCreatedDate() );
        claimResponse.processedDate( claim.getProcessedDate() );
        claimResponse.rejectionReason( claim.getRejectionReason() );
        claimResponse.reviewedBy( claim.getReviewedBy() );
        claimResponse.reviewedAt( claim.getReviewedAt() );
        claimResponse.reviewNotes( claim.getReviewNotes() );
        claimResponse.riskScore( claim.getRiskScore() );
        claimResponse.riskFlags( claim.getRiskFlags() );
        claimResponse.healthScore( claim.getHealthScore() );
        if ( claim.getRiskLevel() != null ) {
            claimResponse.riskLevel( claim.getRiskLevel().name() );
        }
        claimResponse.aiSummary( claim.getAiSummary() );
        claimResponse.patientName( claim.getPatientName() );
        claimResponse.hospitalName( claim.getHospitalName() );
        claimResponse.admissionDate( claim.getAdmissionDate() );
        claimResponse.dischargeDate( claim.getDischargeDate() );
        claimResponse.totalBillAmount( claim.getTotalBillAmount() );
        claimResponse.diagnosis( claim.getDiagnosis() );
        claimResponse.claimType( claim.getClaimType() );

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

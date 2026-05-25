package com.tpa.mapper;

import com.tpa.dto.request.claim.ClaimRequest;
import com.tpa.dto.response.claim.ClaimQueryResponse;
import com.tpa.dto.response.claim.ClaimResponse;
import com.tpa.dto.response.claim.ClaimTimelineResponse;
import com.tpa.entity.Claim;
import com.tpa.entity.ClaimQuery;
import com.tpa.entity.ClaimStatusTimeline;
import com.tpa.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-25T17:23:49+0530",
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

        claimResponse.userName( claimUserUsername( claim ) );
        claimResponse.userEmail( claimUserEmail( claim ) );
        claimResponse.admissionDate( claim.getAdmissionDate() );
        claimResponse.aiSummary( claim.getAiSummary() );
        claimResponse.amount( claim.getAmount() );
        claimResponse.claimStatus( claim.getClaimStatus() );
        claimResponse.claimType( claim.getClaimType() );
        claimResponse.createdDate( claim.getCreatedDate() );
        claimResponse.diagnosis( claim.getDiagnosis() );
        claimResponse.dischargeDate( claim.getDischargeDate() );
        if ( claim.getHealthScore() != null ) {
            claimResponse.healthScore( claim.getHealthScore().intValue() );
        }
        claimResponse.hospitalName( claim.getHospitalName() );
        claimResponse.id( claim.getId() );
        claimResponse.patientName( claim.getPatientName() );
        claimResponse.policyId( claim.getPolicyId() );
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

    @Override
    public Claim toClaim(ClaimRequest claimRequest) {
        if ( claimRequest == null ) {
            return null;
        }

        Claim.ClaimBuilder claim = Claim.builder();

        claim.amount( claimRequest.getClaimedAmount() );
        claim.patientName( claimRequest.getClaimFormPatientName() );
        claim.hospitalName( claimRequest.getClaimFormHospitalName() );
        claim.admissionDate( claimRequest.getClaimFormAdmissionDate() );
        claim.dischargeDate( claimRequest.getClaimFormDischargeDate() );
        claim.billDate( claimRequest.getBillDate() );
        claim.billNumber( claimRequest.getBillNumber() );
        claim.carrierName( claimRequest.getCarrierName() );
        claim.claimType( claimRequest.getClaimType() );
        claim.diagnosis( claimRequest.getDiagnosis() );
        claim.policyId( claimRequest.getPolicyId() );
        claim.policyName( claimRequest.getPolicyName() );
        claim.policyNumber( claimRequest.getPolicyNumber() );
        claim.totalBillAmount( claimRequest.getTotalBillAmount() );

        return claim.build();
    }

    @Override
    public void updateEntityFromDto(ClaimRequest claimRequest, Claim claim) {
        if ( claimRequest == null ) {
            return;
        }

        claim.setPolicyId( claimRequest.getPolicyId() );
        claim.setPolicyName( claimRequest.getPolicyName() );
        claim.setPolicyNumber( claimRequest.getPolicyNumber() );
        claim.setAmount( claimRequest.getClaimedAmount() );
        claim.setTotalBillAmount( claimRequest.getTotalBillAmount() );
        claim.setCarrierName( claimRequest.getCarrierName() );
        claim.setClaimType( claimRequest.getClaimType() );
        claim.setDiagnosis( claimRequest.getDiagnosis() );
        claim.setBillNumber( claimRequest.getBillNumber() );
        claim.setBillDate( claimRequest.getBillDate() );
    }

    @Override
    public ClaimQueryResponse toClaimQueryResponse(ClaimQuery claimQuery) {
        if ( claimQuery == null ) {
            return null;
        }

        ClaimQueryResponse.ClaimQueryResponseBuilder claimQueryResponse = ClaimQueryResponse.builder();

        claimQueryResponse.claimId( claimQueryClaimId( claimQuery ) );
        claimQueryResponse.username( claimQuery.getSenderUsername() );
        claimQueryResponse.carrier( claimQuery.isCarrierQuery() );
        claimQueryResponse.id( claimQuery.getId() );
        claimQueryResponse.message( claimQuery.getMessage() );
        claimQueryResponse.timestamp( claimQuery.getTimestamp() );

        return claimQueryResponse.build();
    }

    @Override
    public List<ClaimQueryResponse> toClaimQueryResponses(List<ClaimQuery> claimQueries) {
        if ( claimQueries == null ) {
            return null;
        }

        List<ClaimQueryResponse> list = new ArrayList<ClaimQueryResponse>( claimQueries.size() );
        for ( ClaimQuery claimQuery : claimQueries ) {
            list.add( toClaimQueryResponse( claimQuery ) );
        }

        return list;
    }

    @Override
    public ClaimTimelineResponse toClaimTimelineResponse(ClaimStatusTimeline timeline) {
        if ( timeline == null ) {
            return null;
        }

        ClaimTimelineResponse.ClaimTimelineResponseBuilder claimTimelineResponse = ClaimTimelineResponse.builder();

        claimTimelineResponse.changedBy( timeline.getChangedBy() );
        claimTimelineResponse.claimId( timeline.getClaimId() );
        claimTimelineResponse.fromStatus( timeline.getFromStatus() );
        claimTimelineResponse.id( timeline.getId() );
        claimTimelineResponse.notes( timeline.getNotes() );
        claimTimelineResponse.occurredAt( timeline.getOccurredAt() );
        claimTimelineResponse.toStatus( timeline.getToStatus() );

        return claimTimelineResponse.build();
    }

    @Override
    public List<ClaimTimelineResponse> toClaimTimelineResponses(List<ClaimStatusTimeline> timelines) {
        if ( timelines == null ) {
            return null;
        }

        List<ClaimTimelineResponse> list = new ArrayList<ClaimTimelineResponse>( timelines.size() );
        for ( ClaimStatusTimeline claimStatusTimeline : timelines ) {
            list.add( toClaimTimelineResponse( claimStatusTimeline ) );
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

    private Long claimQueryClaimId(ClaimQuery claimQuery) {
        if ( claimQuery == null ) {
            return null;
        }
        Claim claim = claimQuery.getClaim();
        if ( claim == null ) {
            return null;
        }
        Long id = claim.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}

package com.tpa.mapper;

import com.tpa.dto.request.claim.ClaimRequest;
import com.tpa.dto.response.claim.ClaimQueryResponse;
import com.tpa.dto.response.claim.ClaimResponse;
import com.tpa.dto.response.claim.ClaimTimelineResponse;
import com.tpa.entity.Claim;
import com.tpa.entity.ClaimQuery;
import com.tpa.entity.ClaimStatusTimeline;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserMapper.class, CarrierMapper.class})
public interface ClaimMapper {

    @Mapping(source = "user.username", target = "userName")
    @Mapping(source = "user.email", target = "userEmail")
    // healthScore implicitly converted from Double to Integer if MapStruct supports it, otherwise handled.
    ClaimResponse toClaimResponse(Claim claim);

    @Mapping(source = "user.username", target = "userName")
    @Mapping(source = "user.email", target = "userEmail")
    List<ClaimResponse> toClaimResponses(List<Claim> claims);

    @Mapping(source = "claimedAmount", target = "amount")
    @Mapping(source = "claimFormPatientName", target = "patientName")
    @Mapping(source = "claimFormHospitalName", target = "hospitalName")
    @Mapping(source = "claimFormAdmissionDate", target = "admissionDate")
    @Mapping(source = "claimFormDischargeDate", target = "dischargeDate")
    Claim toClaim(ClaimRequest claimRequest);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "policyId", target = "policyId")
    @Mapping(source = "policyName", target = "policyName")
    @Mapping(source = "policyNumber", target = "policyNumber")
    @Mapping(source = "claimedAmount", target = "amount")
    @Mapping(source = "totalBillAmount", target = "totalBillAmount")
    @Mapping(source = "carrierName", target = "carrierName")
    @Mapping(source = "claimType", target = "claimType")
    @Mapping(source = "diagnosis", target = "diagnosis")
    @Mapping(source = "billNumber", target = "billNumber")
    @Mapping(source = "billDate", target = "billDate")
    void updateEntityFromDto(ClaimRequest claimRequest, @MappingTarget Claim claim);

    @Mapping(source = "claim.id", target = "claimId")
    @Mapping(source = "senderUsername", target = "username")
    @Mapping(source = "carrierQuery", target = "carrier")
    ClaimQueryResponse toClaimQueryResponse(ClaimQuery claimQuery);

    List<ClaimQueryResponse> toClaimQueryResponses(List<ClaimQuery> claimQueries);

    ClaimTimelineResponse toClaimTimelineResponse(ClaimStatusTimeline timeline);

    List<ClaimTimelineResponse> toClaimTimelineResponses(List<ClaimStatusTimeline> timelines);
}

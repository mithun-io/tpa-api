package com.tpa.mapper;

import com.tpa.dto.response.CarrierResponse;
import com.tpa.entity.Carrier;
import com.tpa.entity.User;
import com.tpa.enums.UserStatus;
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
public class CarrierMapperImpl implements CarrierMapper {

    @Override
    public CarrierResponse toCarrierResponse(Carrier carrier) {
        if ( carrier == null ) {
            return null;
        }

        CarrierResponse.CarrierResponseBuilder carrierResponse = CarrierResponse.builder();

        carrierResponse.email( carrierUserEmail( carrier ) );
        carrierResponse.mobile( carrierUserMobile( carrier ) );
        carrierResponse.userStatus( carrierUserUserStatus( carrier ) );
        if ( carrier.getAiRecommendation() != null ) {
            carrierResponse.aiRecommendation( carrier.getAiRecommendation().name() );
        }
        carrierResponse.aiRiskScore( carrier.getAiRiskScore() );
        if ( carrier.getAiRiskStatus() != null ) {
            carrierResponse.aiRiskStatus( carrier.getAiRiskStatus().name() );
        }
        carrierResponse.companyName( carrier.getCompanyName() );
        carrierResponse.companyType( carrier.getCompanyType() );
        carrierResponse.contactPersonName( carrier.getContactPersonName() );
        carrierResponse.contactPersonPhone( carrier.getContactPersonPhone() );
        carrierResponse.id( carrier.getId() );
        carrierResponse.licenseNumber( carrier.getLicenseNumber() );
        carrierResponse.registrationNumber( carrier.getRegistrationNumber() );
        carrierResponse.taxId( carrier.getTaxId() );
        carrierResponse.website( carrier.getWebsite() );

        return carrierResponse.build();
    }

    @Override
    public List<CarrierResponse> toCarrierResponses(List<Carrier> carriers) {
        if ( carriers == null ) {
            return null;
        }

        List<CarrierResponse> list = new ArrayList<CarrierResponse>( carriers.size() );
        for ( Carrier carrier : carriers ) {
            list.add( toCarrierResponse( carrier ) );
        }

        return list;
    }

    private String carrierUserEmail(Carrier carrier) {
        if ( carrier == null ) {
            return null;
        }
        User user = carrier.getUser();
        if ( user == null ) {
            return null;
        }
        String email = user.getEmail();
        if ( email == null ) {
            return null;
        }
        return email;
    }

    private String carrierUserMobile(Carrier carrier) {
        if ( carrier == null ) {
            return null;
        }
        User user = carrier.getUser();
        if ( user == null ) {
            return null;
        }
        String mobile = user.getMobile();
        if ( mobile == null ) {
            return null;
        }
        return mobile;
    }

    private UserStatus carrierUserUserStatus(Carrier carrier) {
        if ( carrier == null ) {
            return null;
        }
        User user = carrier.getUser();
        if ( user == null ) {
            return null;
        }
        UserStatus userStatus = user.getUserStatus();
        if ( userStatus == null ) {
            return null;
        }
        return userStatus;
    }
}

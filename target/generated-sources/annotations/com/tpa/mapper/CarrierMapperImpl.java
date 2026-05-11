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
    date = "2026-05-11T12:55:18+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
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
        carrierResponse.id( carrier.getId() );
        carrierResponse.companyName( carrier.getCompanyName() );
        carrierResponse.companyType( carrier.getCompanyType() );
        carrierResponse.licenseNumber( carrier.getLicenseNumber() );
        carrierResponse.registrationNumber( carrier.getRegistrationNumber() );
        carrierResponse.taxId( carrier.getTaxId() );
        carrierResponse.contactPersonName( carrier.getContactPersonName() );
        carrierResponse.contactPersonPhone( carrier.getContactPersonPhone() );
        carrierResponse.website( carrier.getWebsite() );
        carrierResponse.aiRiskScore( carrier.getAiRiskScore() );
        if ( carrier.getAiRiskStatus() != null ) {
            carrierResponse.aiRiskStatus( carrier.getAiRiskStatus().name() );
        }
        if ( carrier.getAiRecommendation() != null ) {
            carrierResponse.aiRecommendation( carrier.getAiRecommendation().name() );
        }

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

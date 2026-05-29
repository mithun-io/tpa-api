package com.tpa.mapper;

import com.tpa.dto.request.user.CarrierRequest;
import com.tpa.dto.response.user.CarrierResponse;
import com.tpa.entity.Carrier;
import com.tpa.entity.User;
import com.tpa.enums.UserStatus;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-29T12:56:59+0530",
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
        carrierResponse.phoneNumber( carrierUserPhoneNumber( carrier ) );
        carrierResponse.userStatus( carrierUserUserStatus( carrier ) );
        carrierResponse.contactPersonName( carrierUserUsername( carrier ) );
        carrierResponse.contactPersonPhone( carrierUserPhoneNumber( carrier ) );
        carrierResponse.id( carrier.getId() );
        carrierResponse.companyName( carrier.getCompanyName() );
        carrierResponse.companyType( carrier.getCompanyType() );
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

    @Override
    public Carrier toCarrier(CarrierRequest carrierRequest) {
        if ( carrierRequest == null ) {
            return null;
        }

        Carrier.CarrierBuilder carrier = Carrier.builder();

        carrier.user( carrierRequestToUser( carrierRequest ) );
        carrier.companyName( carrierRequest.getCompanyName() );
        carrier.companyType( carrierRequest.getCompanyType() );
        carrier.registrationNumber( carrierRequest.getRegistrationNumber() );
        carrier.licenseNumber( carrierRequest.getLicenseNumber() );
        carrier.taxId( carrierRequest.getTaxId() );
        carrier.website( carrierRequest.getWebsite() );

        return carrier.build();
    }

    @Override
    public void updateEntityFromDto(CarrierRequest carrierRequest, Carrier carrier) {
        if ( carrierRequest == null ) {
            return;
        }

        carrier.setCompanyName( carrierRequest.getCompanyName() );
        carrier.setCompanyType( carrierRequest.getCompanyType() );
        carrier.setRegistrationNumber( carrierRequest.getRegistrationNumber() );
        carrier.setLicenseNumber( carrierRequest.getLicenseNumber() );
        carrier.setTaxId( carrierRequest.getTaxId() );
        carrier.setWebsite( carrierRequest.getWebsite() );
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

    private String carrierUserPhoneNumber(Carrier carrier) {
        if ( carrier == null ) {
            return null;
        }
        User user = carrier.getUser();
        if ( user == null ) {
            return null;
        }
        String phoneNumber = user.getPhoneNumber();
        if ( phoneNumber == null ) {
            return null;
        }
        return phoneNumber;
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

    private String carrierUserUsername(Carrier carrier) {
        if ( carrier == null ) {
            return null;
        }
        User user = carrier.getUser();
        if ( user == null ) {
            return null;
        }
        String username = user.getUsername();
        if ( username == null ) {
            return null;
        }
        return username;
    }

    protected User carrierRequestToUser(CarrierRequest carrierRequest) {
        if ( carrierRequest == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.username( carrierRequest.getName() );
        user.email( carrierRequest.getEmail() );
        user.phoneNumber( carrierRequest.getPhoneNumber() );
        user.address( carrierRequest.getAddress() );

        return user.build();
    }
}

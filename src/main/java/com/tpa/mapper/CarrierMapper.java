package com.tpa.mapper;

import com.tpa.dto.request.user.CarrierRequest;
import com.tpa.dto.response.user.CarrierResponse;
import com.tpa.entity.Carrier;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = UserMapper.class)
public interface CarrierMapper {

    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.phoneNumber", target = "phoneNumber")
    @Mapping(source = "user.userStatus", target = "userStatus")
    @Mapping(source = "user.username", target = "contactPersonName")
    @Mapping(source = "user.phoneNumber", target = "contactPersonPhone")
    CarrierResponse toCarrierResponse(Carrier carrier);

    List<CarrierResponse> toCarrierResponses(List<Carrier> carriers);

    @Mapping(source = "name", target = "user.username")
    @Mapping(source = "email", target = "user.email")
    @Mapping(source = "mobile", target = "user.phoneNumber")
    @Mapping(source = "address", target = "user.address")
    @Mapping(target = "user.password", ignore = true)
    Carrier toCarrier(CarrierRequest carrierRequest);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "companyName", target = "companyName")
    @Mapping(source = "companyType", target = "companyType")
    @Mapping(source = "registrationNumber", target = "registrationNumber")
    @Mapping(source = "licenseNumber", target = "licenseNumber")
    @Mapping(source = "taxId", target = "taxId")
    @Mapping(source = "website", target = "website")
    void updateEntityFromDto(CarrierRequest carrierRequest, @MappingTarget Carrier carrier);
}
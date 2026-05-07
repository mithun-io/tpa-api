package com.tpa.mapper;

import com.tpa.dto.response.CarrierResponse;
import com.tpa.entity.Carrier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarrierMapper {

    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.mobile", target = "mobile")
    @Mapping(source = "user.userStatus", target = "userStatus")
    CarrierResponse toCarrierResponse(Carrier carrier);

    List<CarrierResponse> toCarrierResponses(List<Carrier> carriers);
}

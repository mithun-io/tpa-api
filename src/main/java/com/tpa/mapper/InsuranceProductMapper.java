package com.tpa.mapper;

import com.tpa.dto.request.InsuranceProductRequest;
import com.tpa.dto.response.InsuranceProductResponse;
import com.tpa.entity.InsuranceProduct;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InsuranceProductMapper {

    @Mapping(source = "carrier.id", target = "carrierId")
    @Mapping(source = "carrier.companyName", target = "carrierName")
    InsuranceProductResponse toInsuranceProductResponse(InsuranceProduct insuranceProduct);

    List<InsuranceProductResponse> toInsuranceProductResponses(List<InsuranceProduct> insuranceProducts);

    @Mapping(target = "carrier", ignore = true) // Set carrier in service
    InsuranceProduct toInsuranceProduct(InsuranceProductRequest insuranceProductRequest);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "productName", target = "productName")
    @Mapping(source = "productCode", target = "productCode")
    @Mapping(source = "policyType", target = "policyType")
    @Mapping(source = "coverageAmount", target = "coverageAmount")
    @Mapping(source = "premiumAmount", target = "premiumAmount")
    @Mapping(source = "waitingPeriodDays", target = "waitingPeriodDays")
    @Mapping(source = "active", target = "active")
    void updateEntityFromDto(InsuranceProductRequest insuranceProductRequest, @MappingTarget InsuranceProduct insuranceProduct);
}

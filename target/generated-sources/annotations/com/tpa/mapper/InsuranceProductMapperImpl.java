package com.tpa.mapper;

import com.tpa.dto.request.claim.InsuranceProductRequest;
import com.tpa.dto.response.claim.InsuranceProductResponse;
import com.tpa.entity.Carrier;
import com.tpa.entity.InsuranceProduct;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-17T09:13:19+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class InsuranceProductMapperImpl implements InsuranceProductMapper {

    @Override
    public InsuranceProductResponse toInsuranceProductResponse(InsuranceProduct insuranceProduct) {
        if ( insuranceProduct == null ) {
            return null;
        }

        InsuranceProductResponse.InsuranceProductResponseBuilder insuranceProductResponse = InsuranceProductResponse.builder();

        insuranceProductResponse.carrierId( insuranceProductCarrierId( insuranceProduct ) );
        insuranceProductResponse.carrierName( insuranceProductCarrierCompanyName( insuranceProduct ) );
        insuranceProductResponse.active( insuranceProduct.getActive() );
        insuranceProductResponse.coverageAmount( insuranceProduct.getCoverageAmount() );
        insuranceProductResponse.id( insuranceProduct.getId() );
        insuranceProductResponse.policyType( insuranceProduct.getPolicyType() );
        insuranceProductResponse.premiumAmount( insuranceProduct.getPremiumAmount() );
        insuranceProductResponse.productCode( insuranceProduct.getProductCode() );
        insuranceProductResponse.productName( insuranceProduct.getProductName() );
        insuranceProductResponse.waitingPeriodDays( insuranceProduct.getWaitingPeriodDays() );

        return insuranceProductResponse.build();
    }

    @Override
    public List<InsuranceProductResponse> toInsuranceProductResponses(List<InsuranceProduct> insuranceProducts) {
        if ( insuranceProducts == null ) {
            return null;
        }

        List<InsuranceProductResponse> list = new ArrayList<InsuranceProductResponse>( insuranceProducts.size() );
        for ( InsuranceProduct insuranceProduct : insuranceProducts ) {
            list.add( toInsuranceProductResponse( insuranceProduct ) );
        }

        return list;
    }

    @Override
    public InsuranceProduct toInsuranceProduct(InsuranceProductRequest insuranceProductRequest) {
        if ( insuranceProductRequest == null ) {
            return null;
        }

        InsuranceProduct.InsuranceProductBuilder insuranceProduct = InsuranceProduct.builder();

        insuranceProduct.active( insuranceProductRequest.getActive() );
        insuranceProduct.coverageAmount( insuranceProductRequest.getCoverageAmount() );
        insuranceProduct.policyType( insuranceProductRequest.getPolicyType() );
        insuranceProduct.premiumAmount( insuranceProductRequest.getPremiumAmount() );
        insuranceProduct.productCode( insuranceProductRequest.getProductCode() );
        insuranceProduct.productName( insuranceProductRequest.getProductName() );
        insuranceProduct.waitingPeriodDays( insuranceProductRequest.getWaitingPeriodDays() );

        return insuranceProduct.build();
    }

    @Override
    public void updateEntityFromDto(InsuranceProductRequest insuranceProductRequest, InsuranceProduct insuranceProduct) {
        if ( insuranceProductRequest == null ) {
            return;
        }

        insuranceProduct.setProductName( insuranceProductRequest.getProductName() );
        insuranceProduct.setProductCode( insuranceProductRequest.getProductCode() );
        insuranceProduct.setPolicyType( insuranceProductRequest.getPolicyType() );
        insuranceProduct.setCoverageAmount( insuranceProductRequest.getCoverageAmount() );
        insuranceProduct.setPremiumAmount( insuranceProductRequest.getPremiumAmount() );
        insuranceProduct.setWaitingPeriodDays( insuranceProductRequest.getWaitingPeriodDays() );
        insuranceProduct.setActive( insuranceProductRequest.getActive() );
    }

    private Long insuranceProductCarrierId(InsuranceProduct insuranceProduct) {
        if ( insuranceProduct == null ) {
            return null;
        }
        Carrier carrier = insuranceProduct.getCarrier();
        if ( carrier == null ) {
            return null;
        }
        Long id = carrier.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String insuranceProductCarrierCompanyName(InsuranceProduct insuranceProduct) {
        if ( insuranceProduct == null ) {
            return null;
        }
        Carrier carrier = insuranceProduct.getCarrier();
        if ( carrier == null ) {
            return null;
        }
        String companyName = carrier.getCompanyName();
        if ( companyName == null ) {
            return null;
        }
        return companyName;
    }
}

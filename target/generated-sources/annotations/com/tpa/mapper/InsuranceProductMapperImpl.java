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
    date = "2026-06-02T12:27:35+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
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
        insuranceProductResponse.id( insuranceProduct.getId() );
        insuranceProductResponse.productName( insuranceProduct.getProductName() );
        insuranceProductResponse.productCode( insuranceProduct.getProductCode() );
        insuranceProductResponse.policyType( insuranceProduct.getPolicyType() );
        insuranceProductResponse.coverageAmount( insuranceProduct.getCoverageAmount() );
        insuranceProductResponse.premiumAmount( insuranceProduct.getPremiumAmount() );
        insuranceProductResponse.waitingPeriodDays( insuranceProduct.getWaitingPeriodDays() );
        insuranceProductResponse.active( insuranceProduct.getActive() );

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

        insuranceProduct.productName( insuranceProductRequest.getProductName() );
        insuranceProduct.productCode( insuranceProductRequest.getProductCode() );
        insuranceProduct.policyType( insuranceProductRequest.getPolicyType() );
        insuranceProduct.coverageAmount( insuranceProductRequest.getCoverageAmount() );
        insuranceProduct.premiumAmount( insuranceProductRequest.getPremiumAmount() );
        insuranceProduct.waitingPeriodDays( insuranceProductRequest.getWaitingPeriodDays() );
        insuranceProduct.active( insuranceProductRequest.getActive() );

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

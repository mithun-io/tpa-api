package com.tpa.mapper;

import com.tpa.dto.request.CustomerRequest;
import com.tpa.dto.response.CustomerResponse;
import com.tpa.entity.Customer;
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
public class CustomerMapperImpl implements CustomerMapper {

    @Override
    public Customer toCustomer(CustomerRequest customerRequest) {
        if ( customerRequest == null ) {
            return null;
        }

        Customer.CustomerBuilder customer = Customer.builder();

        return customer.build();
    }

    @Override
    public CustomerResponse toCustomerResponse(Customer customer) {
        if ( customer == null ) {
            return null;
        }

        CustomerResponse customerResponse = new CustomerResponse();

        customerResponse.setId( customer.getId() );

        return customerResponse;
    }

    @Override
    public List<CustomerResponse> toCustomerResponses(List<Customer> customers) {
        if ( customers == null ) {
            return null;
        }

        List<CustomerResponse> list = new ArrayList<CustomerResponse>( customers.size() );
        for ( Customer customer : customers ) {
            list.add( toCustomerResponse( customer ) );
        }

        return list;
    }
}

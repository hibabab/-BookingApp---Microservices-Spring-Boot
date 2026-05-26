package com.hiba.booking.customer;

import com.hiba.booking.exception.CustomerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    public String createCustomer(CustomerRequest request) {
        var customer = this.repository.save(mapper.toCustomer(request));
        return customer.getId();
    }


    public void updateCustomer(CustomerRequest request, String id) {
        Optional<Customer> customerOptional = repository.findById(id);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            mergeCustomer(customer, request);
            repository.save(customer);
        } else {
            throw new CustomerNotFoundException(
                    "Cannot update customer:: No customer found with the provided ID: " + id
            );
        }
    }


    private void mergeCustomer(Customer customer, CustomerRequest request) {

        if (StringUtils.isNotBlank(request.email())) {
            customer.setEmail(request.email());
        }
        // phone is primitive int in your record — update if > 0 (simple convention for partial update)
        if (request.phone() > 0) {
            customer.setPhone(request.phone());
        }
        if (request.address() != null) {
            customer.setAddress(request.address());
        }
    }

    public List<CustomerResponse> findAllCustomers() {
        List<Customer> customers = repository.findAll();
        List<CustomerResponse> responses = new ArrayList<>();

        for (Customer customer : customers) {
            responses.add(mapper.fromCustomer(customer));
        }

        return responses;
    }


    public CustomerResponse findById(String id) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "No customer found with the provided ID: " + id
                ));
        return mapper.fromCustomer(customer);
    }

    public boolean existsById(String id) {
        return this.repository.findById(id)
                .isPresent();
    }

    public void deleteCustomer(String id) {
        this.repository.deleteById(id);
    }
}
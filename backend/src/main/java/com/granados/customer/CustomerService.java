package com.granados.customer;

import com.granados.exception.DuplicateResourceException;
import com.granados.exception.RequestValidationException;
import com.granados.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerDao customerDAO;
    private final CustomerDTOMapper customerDTOMapper;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDAO, CustomerDTOMapper customerDTOMapper, PasswordEncoder passwordEncoder) {
        this.customerDAO = customerDAO;
        this.customerDTOMapper = customerDTOMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<CustomerDTO> getAllCustomers() {
        return customerDAO.selectAllCustomers()
                .stream()
                .map(customerDTOMapper)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomer(Integer customerId) {
        return customerDAO.selectCustomerById(customerId)
                .map(customerDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "customers with id [%s] does not exists".formatted(customerId)
                ));
    }

    public void addCustomer(
            CustomerRegistrationRequest customerRegistrationRequest) {
        //check if emails exists
        String email = customerRegistrationRequest.email();
        if (customerDAO.existsCustomerWithEmail(email)) {
            throw new DuplicateResourceException(
                    "email already taken".formatted(email));
        }

        //add
        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                passwordEncoder.encode(customerRegistrationRequest.password()),
                customerRegistrationRequest.age(),
                customerRegistrationRequest.gender()
        );

        customerDAO.insertCustomer(customer);

    }

    public void deleteCustomerById(Integer customerId){
        if(!customerDAO.existsCustomerWithId(customerId)){
            throw new ResourceNotFoundException(
                    "customer with id [%s] not found".formatted(customerId)
            );
        }
        customerDAO.deleteCustomerById(customerId);
    }

    public void updateCustomer(Integer customerId, CustomerUpdateRequest updateRequest) {
        Customer customer = customerDAO.selectCustomerById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "customers with id [%s] does not exists".formatted(customerId)
                ));

        boolean changes = false;

        if(updateRequest.name() != null && !updateRequest.name().equals(customer.getName())){
            customer.setName(updateRequest.name());
            changes = true;
        }

        if(updateRequest.age() != null && !updateRequest.age().equals(customer.getAge())){
            customer.setAge(updateRequest.age());
            changes = true;
        }

        if(updateRequest.gender() != null && !updateRequest.gender().equals(customer.getGender())){
            customer.setGender(updateRequest.gender());
            changes = true;
        }

        if(updateRequest.email() != null && !updateRequest.email().equals(customer.getEmail())){
            if (customerDAO.existsCustomerWithEmail(updateRequest.email())){
                throw new DuplicateResourceException("email already taken");
            }
            customer.setEmail(updateRequest.email());
            changes = true;
        }

        if(!changes){
            throw new RequestValidationException("no data changes found");
        }

        customerDAO.updateCustomer(customer);
    }
}

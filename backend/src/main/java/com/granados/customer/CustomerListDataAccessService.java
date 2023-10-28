package com.granados.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list")
public class CustomerListDataAccessService implements CustomerDao {

    //db
    private static List<Customer> customers;

    static {
        customers = new ArrayList<>();
        Customer german = new Customer(
                1,
                "German",
                "german@gmail.com",
                "password",
                28,
                Gender.MALE
        );

        Customer mateo = new Customer(
                2,
                "Mateo",
                "mateo@gmail.com",
                "password",
                12,
                Gender.MALE
        );


        customers.add(german);
        customers.add(mateo);
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer customerId) {
        return customers
                .stream()
                .filter(customer -> customer.getId().equals(customerId))
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        customers.add(customer);
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        return customers.stream()
                .anyMatch(customer -> customer.getEmail().equals(email));
    }

    @Override
    public boolean existsCustomerWithId(Integer customerId) {
        return customers.stream()
                .anyMatch(customer -> customer.getId().equals(customerId));
    }

    @Override
    public void deleteCustomerById(Integer customerId) {
        customers.stream()
                .filter(customer -> customer.getId().equals(customerId))
                .findFirst()
                .ifPresent(customers::remove);
    }

    @Override
    public void updateCustomer(Customer customer) {
        customers.add(customer);
    }

    @Override
    public Optional<Customer> selectUserByEmail(String email) {
        return customers
                .stream()
                .filter(customer -> customer.getUsername().equals(email))
                .findFirst();
    }
}

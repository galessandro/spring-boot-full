package com.granados;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.granados.customer.Customer;
import com.granados.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository){
        return args -> {
            Faker faker = new Faker();
            Name name = faker.name();
            String firstName = name.firstName();
            String lastName = name.lastName();
            String fullName = firstName + " " + lastName;
            String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@gmail.com";
            Customer customer = new Customer(
                    fullName,
                    email,
                    faker.random().nextInt(5, 100)
            );

            customerRepository.save(customer);
        };
    }
}

package com.granados;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.granados.customer.Customer;
import com.granados.customer.CustomerRepository;
import com.granados.customer.Gender;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner runner(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder){
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
                    passwordEncoder.encode(UUID.randomUUID().toString()),
                    faker.random().nextInt(5, 100),
                    faker.options().option(Gender.MALE, Gender.FEMALE)
            );

            customerRepository.save(customer);
        };
    }
}

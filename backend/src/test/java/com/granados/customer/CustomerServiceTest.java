package com.granados.customer;

import com.granados.exception.DuplicateResourceException;
import com.granados.exception.RequestValidationException;
import com.granados.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao);
    }

    @Test
    void getAllCustomers() {

        // When
        underTest.getAllCustomers();

        // Then
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        // Given
        int id = 1;
        Customer customer = new Customer(
                id, "German", "german@gmail.com", 28, Gender.MALE
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // When
        Customer actual = underTest.getCustomer(id);

        // Then
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowWhenGetCustomerReturnEmptyOptional() {
        // Given
        int id = 1;
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customers with id [%s] does not exists".formatted(id));
    }

    @Test
    void addCustomer() {
        // Given
        String email = "german@gmail.com";

        when(customerDao.existsCustomerWithEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "german", email, 29, Gender.MALE
        );
        // When
        underTest.addCustomer(request);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );

        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void willThrowWhenEmailExistsWhileAddingACostumer() {
        // Given
        String email = "german@gmail.com";

        when(customerDao.existsCustomerWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "german", email, 29, Gender.MALE
        );
        // When
        // Then
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        verify(customerDao, never()).insertCustomer(any());

    }

    @Test
    void deleteCustomerById() {
        // Given
        int id = 1;

        when(customerDao.existsCustomerWithId(id)).thenReturn(true);
        // When
        underTest.deleteCustomerById(id);
        // Then
        verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void willThrownWhenDeleteCustomerByIdNotFound() {
        // Given
        int id = 1;

        when(customerDao.existsCustomerWithId(id)).thenReturn(false);
        // When
        // Then
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));

        verify(customerDao, never()).deleteCustomerById(id);

    }

    @Test
    void canUpdateAllCustomerProperties() {
        // Given
        int id = 1;
        Customer customer = new Customer(
                id, "German", "german@gmail.com", 28, Gender.MALE
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // When
        String newEmail = "germani@gmail.com";
        CustomerUpdateRequest updateRequest = new
                CustomerUpdateRequest("Germani", newEmail, 23, Gender.MALE);

        when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(false);

        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCostumer = customerArgumentCaptor.getValue();

        assertThat(capturedCostumer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCostumer.getAge()).isEqualTo(updateRequest.age());
        assertThat(capturedCostumer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(capturedCostumer.getGender()).isEqualTo(updateRequest.gender());
    }

    @Test
    void canUpdateOnlyCustomerName() {
        // Given
        int id = 1;
        Customer customer = new Customer(
                id, "German", "german@gmail.com", 28, Gender.MALE
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // When
        CustomerUpdateRequest updateRequest = new
                CustomerUpdateRequest("Germani", null, null, null);


        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCostumer = customerArgumentCaptor.getValue();

        assertThat(capturedCostumer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCostumer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCostumer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCostumer.getGender()).isEqualTo(customer.getGender());
    }

    @Test
    void canUpdateOnlyCustomerEmail() {
        // Given
        int id = 1;
        Customer customer = new Customer(
                id, "German", "german@gmail.com", 28, Gender.MALE
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // When
        String newEmail = "germani@gmail.com";

        CustomerUpdateRequest updateRequest = new
                CustomerUpdateRequest(null, newEmail, null, null);

        when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(false);

        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCostumer = customerArgumentCaptor.getValue();

        assertThat(capturedCostumer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCostumer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCostumer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(capturedCostumer.getGender()).isEqualTo(customer.getGender());
    }


    @Test
    void canUpdateOnlyCustomerAge() {
        // Given
        int id = 1;
        Customer customer = new Customer(
                id, "German", "german@gmail.com", 28, Gender.MALE
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // When
        CustomerUpdateRequest updateRequest = new
                CustomerUpdateRequest(null, null, 30, null);

        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCostumer = customerArgumentCaptor.getValue();

        assertThat(capturedCostumer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCostumer.getAge()).isEqualTo(updateRequest.age());
        assertThat(capturedCostumer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCostumer.getGender()).isEqualTo(customer.getGender());
    }

    @Test
    void canUpdateOnlyCustomerGender() {
        // Given
        int id = 1;
        Customer customer = new Customer(
                id, "German", "german@gmail.com", 28, Gender.MALE
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // When
        CustomerUpdateRequest updateRequest = new
                CustomerUpdateRequest(null, null, null, Gender.FEMALE);


        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCostumer = customerArgumentCaptor.getValue();

        assertThat(capturedCostumer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCostumer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCostumer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCostumer.getGender()).isEqualTo(updateRequest.gender());
    }

    @Test
    void willThrowWhenTryingToUpdateCustomerEmailWhenAlreadyTaken() {
        // Given
        int id = 1;
        Customer customer = new Customer(
                id, "German", "german@gmail.com", 28, Gender.MALE
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // When
        String newEmail = "germani@gmail.com";

        CustomerUpdateRequest updateRequest = new
                CustomerUpdateRequest(null, newEmail, null, null);

        when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(true);

        // Then
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void willThrownWhenCustomerUpdateHasNoChanges() {
        // Given
        int id = 1;
        Customer customer = new Customer(
                id, "German", "german@gmail.com", 28, Gender.MALE
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // When
        CustomerUpdateRequest updateRequest = new
                CustomerUpdateRequest(customer.getName(), customer.getEmail(), customer.getAge(), customer.getGender());

        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("no data changes found");

        // Then
        verify(customerDao, never()).updateCustomer(any());
    }

}
package com.example.maqas.Service;

import com.example.maqas.Api.ApiException;
import com.example.maqas.Model.Customer;
import com.example.maqas.Repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    public void addCustomer(Customer customer) {
        if (customerRepository.findCustomerByEmail(customer.getEmail()) != null) {
            throw new ApiException("Email already exists");
        }
        if (customerRepository.findCustomerByPhoneNumber(customer.getPhoneNumber()) != null) {
            throw new ApiException("Phone number already exists");
        }

        customerRepository.save(customer);
    }

    public void updateCustomer(Integer id, Customer customer) {
        Customer oldCustomer = customerRepository.getCustomerById(id);

        if (oldCustomer == null) {
            throw new ApiException("Customer not found");
        }

        Customer emailCustomer = customerRepository.findCustomerByEmail(customer.getEmail());
        Customer phoneCustomer = customerRepository.findCustomerByPhoneNumber(customer.getPhoneNumber());

        if (emailCustomer != null && !emailCustomer.getId().equals(id)) {
            throw new ApiException("Email already exists");
        }
        if (phoneCustomer != null && !phoneCustomer.getId().equals(id)) {
            throw new ApiException("Phone number already exists");
        }

        oldCustomer.setName(customer.getName());
        oldCustomer.setEmail(customer.getEmail());
        oldCustomer.setPhoneNumber(customer.getPhoneNumber());
        oldCustomer.setPassword(customer.getPassword());
        oldCustomer.setCity(customer.getCity());

        customerRepository.save(oldCustomer);
    }

    public void deleteCustomer(Integer id) {
        Customer selectedCustomer = customerRepository.getCustomerById(id);

        if (selectedCustomer == null) {
            throw new ApiException("Customer not found");
        }

        customerRepository.delete(selectedCustomer);
    }

    public List<Customer> getCustomersByCity(String city) {
        return customerRepository.findCustomersByCity(city);
    }
}

package com.example.maqas.Controller;

import com.example.maqas.Api.ApiException;
import com.example.maqas.Api.ApiResponse;
import com.example.maqas.Model.Customer;
import com.example.maqas.Service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllCustomers() {
        return ResponseEntity.status(200).body(customerService.getCustomers());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCustomer(@RequestBody @Valid Customer customer, Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(message);
        }

        try {
            customerService.addCustomer(customer);
            return ResponseEntity.status(200).body(new ApiResponse("Customer added successfully"));
        } catch (ApiException e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Integer id, @RequestBody @Valid Customer customer, Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(message);
        }

        try {
            customerService.updateCustomer(id, customer);
            return ResponseEntity.status(200).body(new ApiResponse("Customer updated successfully"));
        } catch (ApiException e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Integer id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.status(200).body(new ApiResponse("Customer deleted successfully"));
        } catch (ApiException e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<?> getCustomersByCity(@PathVariable String city) {
        return ResponseEntity.status(200).body(customerService.getCustomersByCity(city));
    }
}

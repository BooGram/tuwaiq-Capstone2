package com.example.maqas.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 40)
    @NotEmpty(message = "Name must not be empty")
    @Size(min = 4, message = "Name must be at least 4 characters")
    private String name;

    @Column(nullable = false, unique = true)
    @NotEmpty(message = "Email must not be empty")
    @Email(message = "Email must be valid")
    private String email;

    @Column(nullable = false, length = 20)
    @NotEmpty(message = "Phone number must not be empty")
    @Pattern(regexp = "^05\\d{8}$", message = "Phone number must start with 05 and contain 10 digits")
    private String phoneNumber;

    @Column(nullable = false, length = 20)
    @NotEmpty(message = "Password must not be empty")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).{6,}$", message = "Password must contain letters and digits and be at least 6 characters")
    private String password;

    @Column(nullable = false, length = 30)
    @NotEmpty(message = "City must not be empty")
    private String city;
}

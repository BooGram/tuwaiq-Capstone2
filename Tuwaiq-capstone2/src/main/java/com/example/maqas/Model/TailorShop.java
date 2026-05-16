package com.example.maqas.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TailorShop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    @NotEmpty(message = "Shop name must not be empty")
    @Size(min = 3, message = "Shop name must be at least 3 characters")
    private String name;

    @Column(nullable = false, length = 40)
    @NotEmpty(message = "Owner name must not be empty")
    private String ownerName;

    @Column(nullable = false, unique = true)
    @NotEmpty(message = "Email must not be empty")
    @Email(message = "Email must be valid")
    private String email;

    @Column(nullable = false, length = 20)
    @NotEmpty(message = "Phone number must not be empty")
    @Pattern(regexp = "^05\\d{8}$", message = "Phone number must start with 05 and contain 10 digits")
    private String phoneNumber;

    @Column(nullable = false, length = 30)
    @NotEmpty(message = "City must not be empty")
    private String city;

    @Column(nullable = false, length = 20)
    @Check(constraints = "specialty IN ('THOBE','ABAYA','DRESS','UNIFORM','ALL')")
    @NotEmpty(message = "Specialty must not be empty")
    @Pattern(regexp = "^(THOBE|ABAYA|DRESS|UNIFORM|ALL)$", message = "Specialty must be THOBE, ABAYA, DRESS, UNIFORM, or ALL")
    private String specialty;

    @Column(nullable = false)
    @NotNull(message = "Rating must not be null")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;
}

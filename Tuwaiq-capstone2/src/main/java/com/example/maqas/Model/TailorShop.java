package com.example.maqas.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
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

    @Column(nullable = false)
    @NotNull(message = "Owner id must not be null")
    private Integer ownerId;

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
    @NotNull(message = "Offers home measurement must not be null")
    private Boolean offersHomeMeasurement;

    @Column
    @PositiveOrZero(message = "Measurement visit price must be zero or positive")
    private Double measurementVisitPrice;

    @Column(nullable = false)
    @NotNull(message = "Free measurement with order must not be null")
    private Boolean freeMeasurementWithOrder;

}

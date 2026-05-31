package com.example.maqas.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MeasurementRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer customerId;

    @Column(nullable = false)
    @NotNull(message = "Tailor shop ID must not be null")
    private Integer tailorShopId;

    @Column(nullable = false, length = 30)
    @NotEmpty(message = "City must not be empty")
    private String city;

    @Column(nullable = false, length = 150)
    @NotEmpty(message = "Address must not be empty")
    private String address;

    @Column(nullable = false)
    @NotNull(message = "Preferred date must not be null")
    @FutureOrPresent(message = "Preferred date must be today or in the future")
    private LocalDate preferredDate;

    @Column(nullable = false, length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
    @Check(constraints = "status IN ('PENDING','ACCEPTED','COMPLETED','CANCELLED')")
    private String status;
}

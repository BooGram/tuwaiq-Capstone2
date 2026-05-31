package com.example.maqas.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer customerId;

    @Column(nullable = false)
    @NotNull(message = "Shoulder measurement must not be null")
    @Positive(message = "Shoulder measurement must be positive")
    private Double shoulder;

    @Column(nullable = false)
    @NotNull(message = "Chest measurement must not be null")
    @Positive(message = "Chest measurement must be positive")
    private Double chest;

    @Column(nullable = false)
    @NotNull(message = "Waist measurement must not be null")
    @Positive(message = "Waist measurement must be positive")
    private Double waist;

    @Column(nullable = false)
    @NotNull(message = "Sleeve length must not be null")
    @Positive(message = "Sleeve length must be positive")
    private Double sleeveLength;

    @Column(nullable = false)
    @NotNull(message = "Height must not be null")
    @Positive(message = "Height must be positive")
    private Double height;

    @Column(columnDefinition = "VARCHAR(200)")
    private String notes;
}

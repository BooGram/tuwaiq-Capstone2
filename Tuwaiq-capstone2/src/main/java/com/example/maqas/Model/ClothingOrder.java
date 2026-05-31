package com.example.maqas.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ClothingOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer customerId;

    @Column(nullable = false)
    @NotNull(message = "Tailor shop id must not be null")
    private Integer tailorShopId;

    @Column(nullable = false)
    @NotNull(message = "Measurement id must not be null")
    private Integer measurementId;

    @Column(nullable = false, length = 20)
    @Check(constraints = "category IN ('THOBE','ABAYA','DRESS','UNIFORM')")
    @NotEmpty(message = "Category must not be empty")
    @Pattern(regexp = "^(THOBE|ABAYA|DRESS|UNIFORM)$", message = "Category must be THOBE, ABAYA, DRESS, or UNIFORM")
    private String category;

    @Column(nullable = false, length = 30)
    @NotEmpty(message = "Fabric type must not be empty")
    private String fabricType;

    @Column(nullable = false, length = 25)
    @NotEmpty(message = "Color must not be empty")
    private String color;

    @Column
    private Double price;

    @Column(nullable = false, length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
    @Check(constraints = "status IN ('PENDING','QUOTED','ACCEPTED','IN_PROGRESS','READY','DELIVERED','CANCELLED','REJECTED')")
    private String status;

    @Column(nullable = false)
    @NotNull(message = "Order date must not be null")
    private LocalDate orderDate;

}

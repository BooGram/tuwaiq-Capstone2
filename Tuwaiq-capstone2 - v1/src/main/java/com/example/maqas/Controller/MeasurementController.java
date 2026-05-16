package com.example.maqas.Controller;

import com.example.maqas.Api.ApiException;
import com.example.maqas.Api.ApiResponse;
import com.example.maqas.Model.Measurement;
import com.example.maqas.Service.MeasurementService;
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
@RequestMapping("/api/v1/measurement")
@RequiredArgsConstructor
public class MeasurementController {

    private final MeasurementService measurementService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllMeasurements() {
        return ResponseEntity.status(200).body(measurementService.getMeasurements());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addMeasurement(@RequestBody @Valid Measurement measurement, Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(message);
        }

        try {
            measurementService.addMeasurement(measurement);
            return ResponseEntity.status(200).body(new ApiResponse("Measurement added successfully"));
        } catch (ApiException e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMeasurement(@PathVariable Integer id, @RequestBody @Valid Measurement measurement, Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(message);
        }

        try {
            measurementService.updateMeasurement(id, measurement);
            return ResponseEntity.status(200).body(new ApiResponse("Measurement updated successfully"));
        } catch (ApiException e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMeasurement(@PathVariable Integer id) {
        try {
            measurementService.deleteMeasurement(id);
            return ResponseEntity.status(200).body(new ApiResponse("Measurement deleted successfully"));
        } catch (ApiException e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getMeasurementsByCustomerId(@PathVariable Integer customerId) {
        try {
            return ResponseEntity.status(200).body(measurementService.getMeasurementsByCustomerId(customerId));
        } catch (ApiException e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }
}

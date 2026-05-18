package com.example.maqas.Controller;

import com.example.maqas.Api.ApiException;
import com.example.maqas.Api.ApiResponse;
import com.example.maqas.Model.MeasurementRequest;
import com.example.maqas.Service.MeasurementRequestService;
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
@RequestMapping("/api/v1/measurement-request")
@RequiredArgsConstructor
public class MeasurementRequestController {

    private final MeasurementRequestService measurementRequestService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllMeasurementRequests() {
        return ResponseEntity.status(200).body(measurementRequestService.getMeasurementRequests());
    }

    @PostMapping("/add/{customerId}")
    public ResponseEntity<?> addMeasurementRequest(@PathVariable Integer customerId, @RequestBody @Valid MeasurementRequest measurementRequest, Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(message);
        }

        try {
            measurementRequestService.addMeasurementRequest(customerId, measurementRequest);
            return ResponseEntity.status(200).body(new ApiResponse("Measurement request added successfully"));
        } catch (ApiException e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMeasurementRequest(@PathVariable Integer id, @RequestBody @Valid MeasurementRequest measurementRequest, Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(message);
        }

        try {
            measurementRequestService.updateMeasurementRequest(id, measurementRequest);
            return ResponseEntity.status(200).body(new ApiResponse("Measurement request updated successfully"));
        } catch (ApiException e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMeasurementRequest(@PathVariable Integer id) {
        try {
            measurementRequestService.deleteMeasurementRequest(id);
            return ResponseEntity.status(200).body(new ApiResponse("Measurement request deleted successfully"));
        } catch (ApiException e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }

    @PutMapping("/change-status/{id}/{status}")
    public ResponseEntity<?> changeMeasurementRequestStatus(@PathVariable Integer id, @PathVariable String status) {
        try {
            measurementRequestService.changeMeasurementRequestStatus(id, status);
            return ResponseEntity.status(200).body(new ApiResponse("Measurement request status changed successfully"));
        } catch (ApiException e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getRequestsByCustomerId(@PathVariable Integer customerId) {
        try {
            return ResponseEntity.status(200).body(measurementRequestService.getRequestsByCustomerId(customerId));
        } catch (ApiException e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }

    @GetMapping("/shop/{tailorShopId}")
    public ResponseEntity<?> getRequestsByTailorShopId(@PathVariable Integer tailorShopId) {
        try {
            return ResponseEntity.status(200).body(measurementRequestService.getRequestsByTailorShopId(tailorShopId));
        } catch (ApiException e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getRequestsByStatus(@PathVariable String status) {
        try {
            return ResponseEntity.status(200).body(measurementRequestService.getRequestsByStatus(status));
        } catch (ApiException e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }
}
package com.example.maqas.Controller;

import com.example.maqas.Api.ApiResponse;
import com.example.maqas.Model.MeasurementRequest;
import com.example.maqas.Service.MeasurementRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> addMeasurementRequest(@PathVariable Integer customerId, @RequestBody @Valid MeasurementRequest measurementRequest) {
        measurementRequestService.addMeasurementRequest(customerId, measurementRequest);
        return ResponseEntity.status(200).body(new ApiResponse("Measurement request added successfully"));
    }

    @PutMapping("/update/{customerId}/{id}")
    public ResponseEntity<?> updateMeasurementRequest(@PathVariable Integer customerId, @PathVariable Integer id, @RequestBody @Valid MeasurementRequest measurementRequest) {
        measurementRequestService.updateMeasurementRequest(customerId, id, measurementRequest);
        return ResponseEntity.status(200).body(new ApiResponse("Measurement request updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMeasurementRequest(@PathVariable Integer id) {
        measurementRequestService.deleteMeasurementRequest(id);
        return ResponseEntity.status(200).body(new ApiResponse("Measurement request deleted successfully"));
    }

    @PutMapping("/change-status/{id}/{status}")
    public ResponseEntity<?> changeMeasurementRequestStatus(@PathVariable Integer id, @PathVariable String status) {
        measurementRequestService.changeMeasurementRequestStatus(id, status);
        return ResponseEntity.status(200).body(new ApiResponse("Measurement request status changed successfully"));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getRequestsByCustomerId(@PathVariable Integer customerId) {
        return ResponseEntity.status(200).body(measurementRequestService.getRequestsByCustomerId(customerId));
    }

    @GetMapping("/shop/{tailorShopId}")
    public ResponseEntity<?> getRequestsByTailorShopId(@PathVariable Integer tailorShopId) {
        return ResponseEntity.status(200).body(measurementRequestService.getRequestsByTailorShopId(tailorShopId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getRequestsByStatus(@PathVariable String status) {
        return ResponseEntity.status(200).body(measurementRequestService.getRequestsByStatus(status));
    }
}

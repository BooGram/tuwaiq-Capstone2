package com.example.maqas.Service;

import com.example.maqas.Api.ApiException;
import com.example.maqas.Model.MeasurementRequest;
import com.example.maqas.Model.TailorShop;
import com.example.maqas.Repository.CustomerRepository;
import com.example.maqas.Repository.MeasurementRequestRepository;
import com.example.maqas.Repository.TailorShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeasurementRequestService {

    private final MeasurementRequestRepository measurementRequestRepository;
    private final CustomerRepository customerRepository;
    private final TailorShopRepository tailorShopRepository;

    public List<MeasurementRequest> getMeasurementRequests() {
        return measurementRequestRepository.findAll();
    }

    public void addMeasurementRequest(Integer customerId, MeasurementRequest measurementRequest) {
        if (customerRepository.getCustomerById(customerId) == null) {
            throw new ApiException("Customer not found");
        }
        measurementRequest.setCustomerId(customerId);
        TailorShop tailorShop = tailorShopRepository.getTailorShopById(measurementRequest.getTailorShopId());
        if (tailorShop == null) {
            throw new ApiException("Tailor shop not found");
        }
        if (tailorShop.getOffersHomeMeasurement().equals(false)) {
            throw new ApiException("Tailor shop does not offer home measurement service");
        }
        if (!measurementRequest.getStatus().equals("PENDING")) {
            throw new ApiException("New measurement request status must be PENDING");
        }

        measurementRequestRepository.save(measurementRequest);
    }

    public void updateMeasurementRequest(Integer id, MeasurementRequest measurementRequest) {
        MeasurementRequest oldMeasurementRequest = measurementRequestRepository.getMeasurementRequestById(id);

        if (oldMeasurementRequest == null) {
            throw new ApiException("Measurement request not found");
        }
        if (customerRepository.getCustomerById(measurementRequest.getCustomerId()) == null) {
            throw new ApiException("Customer not found");
        }
        TailorShop tailorShop = tailorShopRepository.getTailorShopById(measurementRequest.getTailorShopId());
        if (tailorShop == null) {
            throw new ApiException("Tailor shop not found");
        }
        if (tailorShop.getOffersHomeMeasurement().equals(false)) {
            throw new ApiException("Tailor shop does not offer home measurement service");
        }

        oldMeasurementRequest.setCustomerId(measurementRequest.getCustomerId());
        oldMeasurementRequest.setTailorShopId(measurementRequest.getTailorShopId());
        oldMeasurementRequest.setCity(measurementRequest.getCity());
        oldMeasurementRequest.setAddress(measurementRequest.getAddress());
        oldMeasurementRequest.setPreferredDate(measurementRequest.getPreferredDate());
        oldMeasurementRequest.setStatus(measurementRequest.getStatus());

        measurementRequestRepository.save(oldMeasurementRequest);
    }

    public void deleteMeasurementRequest(Integer id) {
        MeasurementRequest selectedMeasurementRequest = measurementRequestRepository.getMeasurementRequestById(id);

        if (selectedMeasurementRequest == null) {
            throw new ApiException("Measurement request not found");
        }

        measurementRequestRepository.delete(selectedMeasurementRequest);
    }

    public void changeMeasurementRequestStatus(Integer id, String status) {
        MeasurementRequest measurementRequest = measurementRequestRepository.getMeasurementRequestById(id);

        if (measurementRequest == null) {
            throw new ApiException("Measurement request not found");
        }
        if (!status.matches("^(PENDING|ACCEPTED|COMPLETED|CANCELLED)$")) {
            throw new ApiException("Status must be PENDING, ACCEPTED, COMPLETED, or CANCELLED");
        }

        measurementRequest.setStatus(status);
        measurementRequestRepository.save(measurementRequest);
    }

    public List<MeasurementRequest> getRequestsByCustomerId(Integer customerId) {
        if (customerRepository.getCustomerById(customerId) == null) {
            throw new ApiException("Customer not found");
        }

        return measurementRequestRepository.findMeasurementRequestsByCustomerId(customerId);
    }

    public List<MeasurementRequest> getRequestsByTailorShopId(Integer tailorShopId) {
        if (tailorShopRepository.getTailorShopById(tailorShopId) == null) {
            throw new ApiException("Tailor shop not found");
        }

        return measurementRequestRepository.findMeasurementRequestsByTailorShopId(tailorShopId);
    }

    public List<MeasurementRequest> getRequestsByStatus(String status) {
        if (!status.matches("^(PENDING|ACCEPTED|COMPLETED|CANCELLED)$")) {
            throw new ApiException("Status must be PENDING, ACCEPTED, COMPLETED, or CANCELLED");
        }

        return measurementRequestRepository.findMeasurementRequestsByStatus(status);
    }
}
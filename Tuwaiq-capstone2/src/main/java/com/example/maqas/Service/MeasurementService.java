package com.example.maqas.Service;

import com.example.maqas.Api.ApiException;
import com.example.maqas.Model.Measurement;
import com.example.maqas.Repository.CustomerRepository;
import com.example.maqas.Repository.MeasurementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeasurementService {

    private final MeasurementRepository measurementRepository;
    private final CustomerRepository customerRepository;

    public List<Measurement> getMeasurements() {
        return measurementRepository.findAll();
    }

    public void addMeasurement(Integer customerId, Measurement measurement) {
        if (customerRepository.getCustomerById(customerId) == null) {
            throw new ApiException("Customer not found");
        }
        measurement.setCustomerId(customerId);
        measurementRepository.save(measurement);
    }

    public void updateMeasurement(Integer customerId, Integer id, Measurement measurement) {
        Measurement oldMeasurement = measurementRepository.getMeasurementById(id);
        if (oldMeasurement == null) {
            throw new ApiException("Measurement not found");
        }
        if (customerRepository.getCustomerById(customerId) == null) {
            throw new ApiException("Customer not found");
        }

        oldMeasurement.setCustomerId(customerId);
        oldMeasurement.setShoulder(measurement.getShoulder());
        oldMeasurement.setChest(measurement.getChest());
        oldMeasurement.setWaist(measurement.getWaist());
        oldMeasurement.setSleeveLength(measurement.getSleeveLength());
        oldMeasurement.setHeight(measurement.getHeight());
        oldMeasurement.setNotes(measurement.getNotes());

        measurementRepository.save(oldMeasurement);
    }

    public void deleteMeasurement(Integer id) {
        Measurement selectedMeasurement = measurementRepository.getMeasurementById(id);
        if (selectedMeasurement == null) {
            throw new ApiException("Measurement not found");
        }
        measurementRepository.delete(selectedMeasurement);
    }

    public List<Measurement> getMeasurementsByCustomerId(Integer customerId) {
        if (customerRepository.getCustomerById(customerId) == null) {
            throw new ApiException("Customer not found");
        }
        return measurementRepository.findMeasurementsByCustomerId(customerId);
    }
}

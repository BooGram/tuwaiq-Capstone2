package com.example.maqas.Repository;

import com.example.maqas.Model.MeasurementRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeasurementRequestRepository extends JpaRepository<MeasurementRequest, Integer> {
    MeasurementRequest getMeasurementRequestById(Integer id);

    List<MeasurementRequest> findMeasurementRequestsByCustomerId(Integer customerId);

    List<MeasurementRequest> findMeasurementRequestsByTailorShopId(Integer tailorShopId);

    List<MeasurementRequest> findMeasurementRequestsByStatus(String status);
}

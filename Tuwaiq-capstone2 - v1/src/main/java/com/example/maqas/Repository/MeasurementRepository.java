package com.example.maqas.Repository;

import com.example.maqas.Model.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, Integer> {
    Measurement getMeasurementById(Integer id);

    List<Measurement> findMeasurementsByCustomerId(Integer customerId);
}

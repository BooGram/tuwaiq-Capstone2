package com.example.maqas.Repository;

import com.example.maqas.Model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    boolean existsByCustomerIdAndShopId(Integer customerId, Integer shopId);

    List<Review> findByShopId(Integer shopId);
}

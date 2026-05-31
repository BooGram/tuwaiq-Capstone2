package com.example.maqas.Repository;

import com.example.maqas.Model.ClothingOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClothingOrderRepository extends JpaRepository<ClothingOrder, Integer> {
    ClothingOrder getClothingOrderById(Integer id);

    List<ClothingOrder> findClothingOrdersByCustomerId(Integer customerId);

    List<ClothingOrder> findClothingOrdersByTailorShopId(Integer tailorShopId);

    List<ClothingOrder> findClothingOrdersByCategory(String category);

    List<ClothingOrder> findClothingOrdersByStatus(String status);

    boolean existsByCustomerIdAndTailorShopIdAndStatus(Integer customerId, Integer tailorShopId, String status);
}

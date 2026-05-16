package com.example.maqas.Repository;

import com.example.maqas.Model.TailorShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TailorShopRepository extends JpaRepository<TailorShop, Integer> {
    TailorShop getTailorShopById(Integer id);

    TailorShop findTailorShopByEmail(String email);

    TailorShop findTailorShopByPhoneNumber(String phoneNumber);

    List<TailorShop> findTailorShopsByCity(String city);

    List<TailorShop> findTailorShopsBySpecialty(String specialty);
}

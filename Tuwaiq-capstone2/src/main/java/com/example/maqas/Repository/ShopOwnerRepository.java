package com.example.maqas.Repository;

import com.example.maqas.Model.ShopOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopOwnerRepository extends JpaRepository<ShopOwner, Integer> {
    ShopOwner getShopOwnerById(Integer id);

    ShopOwner findShopOwnerByEmail(String email);

    ShopOwner findShopOwnerByPhoneNumber(String phoneNumber);
}

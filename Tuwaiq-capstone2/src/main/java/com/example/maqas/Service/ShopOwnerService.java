package com.example.maqas.Service;

import com.example.maqas.Api.ApiException;
import com.example.maqas.Model.ShopOwner;
import com.example.maqas.Repository.ShopOwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopOwnerService {

    private final ShopOwnerRepository shopOwnerRepository;

    public List<ShopOwner> getShopOwners() {
        return shopOwnerRepository.findAll();
    }

    public void addShopOwner(ShopOwner shopOwner) {
        if (shopOwnerRepository.findShopOwnerByEmail(shopOwner.getEmail()) != null) {
            throw new ApiException("Email already exists");
        }
        if (shopOwnerRepository.findShopOwnerByPhoneNumber(shopOwner.getPhoneNumber()) != null) {
            throw new ApiException("Phone number already exists");
        }

        shopOwnerRepository.save(shopOwner);
    }

    public void updateShopOwner(Integer id, ShopOwner shopOwner) {
        ShopOwner oldShopOwner = shopOwnerRepository.getShopOwnerById(id);

        if (oldShopOwner == null) {
            throw new ApiException("Shop owner not found");
        }

        ShopOwner emailShopOwner = shopOwnerRepository.findShopOwnerByEmail(shopOwner.getEmail());
        ShopOwner phoneShopOwner = shopOwnerRepository.findShopOwnerByPhoneNumber(shopOwner.getPhoneNumber());

        if (emailShopOwner != null && !emailShopOwner.getId().equals(id)) {
            throw new ApiException("Email already exists");
        }
        if (phoneShopOwner != null && !phoneShopOwner.getId().equals(id)) {
            throw new ApiException("Phone number already exists");
        }

        oldShopOwner.setName(shopOwner.getName());
        oldShopOwner.setEmail(shopOwner.getEmail());
        oldShopOwner.setPhoneNumber(shopOwner.getPhoneNumber());
        oldShopOwner.setPassword(shopOwner.getPassword());

        shopOwnerRepository.save(oldShopOwner);
    }

    public void deleteShopOwner(Integer id) {
        ShopOwner selectedShopOwner = shopOwnerRepository.getShopOwnerById(id);

        if (selectedShopOwner == null) {
            throw new ApiException("Shop owner not found");
        }

        shopOwnerRepository.delete(selectedShopOwner);
    }
}

package com.example.maqas.Service;

import com.example.maqas.Api.ApiException;
import com.example.maqas.Model.TailorShop;
import com.example.maqas.Repository.TailorShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TailorShopService {

    private final TailorShopRepository tailorShopRepository;

    public List<TailorShop> getTailorShops() {
        return tailorShopRepository.findAll();
    }

    public void addTailorShop(TailorShop tailorShop) {
        if (tailorShopRepository.findTailorShopByEmail(tailorShop.getEmail()) != null) {
            throw new ApiException("Email already exists");
        }
        if (tailorShopRepository.findTailorShopByPhoneNumber(tailorShop.getPhoneNumber()) != null) {
            throw new ApiException("Phone number already exists");
        }

        tailorShopRepository.save(tailorShop);
    }

    public void updateTailorShop(Integer id, TailorShop tailorShop) {
        TailorShop oldTailorShop = tailorShopRepository.getTailorShopById(id);

        if (oldTailorShop == null) {
            throw new ApiException("Tailor shop not found");
        }

        TailorShop emailTailorShop = tailorShopRepository.findTailorShopByEmail(tailorShop.getEmail());
        TailorShop phoneTailorShop = tailorShopRepository.findTailorShopByPhoneNumber(tailorShop.getPhoneNumber());

        if (emailTailorShop != null && !emailTailorShop.getId().equals(id)) {
            throw new ApiException("Email already exists");
        }
        if (phoneTailorShop != null && !phoneTailorShop.getId().equals(id)) {
            throw new ApiException("Phone number already exists");
        }

        oldTailorShop.setName(tailorShop.getName());
        oldTailorShop.setOwnerName(tailorShop.getOwnerName());
        oldTailorShop.setEmail(tailorShop.getEmail());
        oldTailorShop.setPhoneNumber(tailorShop.getPhoneNumber());
        oldTailorShop.setCity(tailorShop.getCity());
        oldTailorShop.setSpecialty(tailorShop.getSpecialty());

        tailorShopRepository.save(oldTailorShop);
    }

    public void deleteTailorShop(Integer id) {
        TailorShop selectedTailorShop = tailorShopRepository.getTailorShopById(id);

        if (selectedTailorShop == null) {
            throw new ApiException("Tailor shop not found");
        }

        tailorShopRepository.delete(selectedTailorShop);
    }

    public List<TailorShop> getTailorShopsByCity(String city) {
        return tailorShopRepository.findTailorShopsByCity(city);
    }

    public List<TailorShop> getTailorShopsBySpecialty(String specialty) {
        return tailorShopRepository.findTailorShopsBySpecialty(specialty);
    }
}

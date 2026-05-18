package com.example.maqas.Service;

import com.example.maqas.Api.ApiException;
import com.example.maqas.Model.TailorShop;
import com.example.maqas.Repository.ShopOwnerRepository;
import com.example.maqas.Repository.TailorShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TailorShopService {

    private final TailorShopRepository tailorShopRepository;
    private final ShopOwnerRepository shopOwnerRepository;

    public List<TailorShop> getTailorShops() {
        return tailorShopRepository.findAll();
    }

    public void addTailorShop(TailorShop tailorShop) {
        if (shopOwnerRepository.getShopOwnerById(tailorShop.getOwnerId()) == null) {
            throw new ApiException("Shop owner not found");
        }
        if (tailorShopRepository.findTailorShopByPhoneNumber(tailorShop.getPhoneNumber()) != null) {
            throw new ApiException("Phone number already exists");
        }
        validateMeasurementPricing(tailorShop);

        tailorShopRepository.save(tailorShop);
    }

    public void updateTailorShop(Integer id, TailorShop tailorShop) {
        TailorShop oldTailorShop = tailorShopRepository.getTailorShopById(id);

        if (oldTailorShop == null) {
            throw new ApiException("Tailor shop not found");
        }
        if (shopOwnerRepository.getShopOwnerById(tailorShop.getOwnerId()) == null) {
            throw new ApiException("Shop owner not found");
        }
        TailorShop phoneTailorShop = tailorShopRepository.findTailorShopByPhoneNumber(tailorShop.getPhoneNumber());

        if (phoneTailorShop != null && !phoneTailorShop.getId().equals(id)) {
            throw new ApiException("Phone number already exists");
        }
        validateMeasurementPricing(tailorShop);

        oldTailorShop.setName(tailorShop.getName());
        oldTailorShop.setOwnerId(tailorShop.getOwnerId());
        oldTailorShop.setPhoneNumber(tailorShop.getPhoneNumber());
        oldTailorShop.setCity(tailorShop.getCity());
        oldTailorShop.setSpecialty(tailorShop.getSpecialty());
        oldTailorShop.setOffersHomeMeasurement(tailorShop.getOffersHomeMeasurement());
        oldTailorShop.setMeasurementVisitPrice(tailorShop.getMeasurementVisitPrice());
        oldTailorShop.setFreeMeasurementWithOrder(tailorShop.getFreeMeasurementWithOrder());

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

    private void validateMeasurementPricing(TailorShop tailorShop) {
        if (tailorShop.getOffersHomeMeasurement().equals(false)) {
            tailorShop.setMeasurementVisitPrice(null);
            tailorShop.setFreeMeasurementWithOrder(false);
            return;
        }
        if (tailorShop.getMeasurementVisitPrice() == null) {
            throw new ApiException("Measurement visit price must be set when shop offers home measurement");
        }
        if (tailorShop.getMeasurementVisitPrice() < 0) {
            throw new ApiException("Measurement visit price must be zero or positive");
        }
    }
}

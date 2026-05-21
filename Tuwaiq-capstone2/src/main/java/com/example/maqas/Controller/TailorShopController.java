package com.example.maqas.Controller;

import com.example.maqas.Api.ApiException;
import com.example.maqas.Api.ApiResponse;
import com.example.maqas.Model.TailorShop;
import com.example.maqas.Service.TailorShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tailor-shop")
@RequiredArgsConstructor
public class TailorShopController {

    private final TailorShopService tailorShopService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllTailorShops() {
        return ResponseEntity.status(200).body(tailorShopService.getTailorShops());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addTailorShop(@RequestBody @Valid TailorShop tailorShop, Errors errors) {
            tailorShopService.addTailorShop(tailorShop);
            return ResponseEntity.status(200).body(new ApiResponse("Tailor shop added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTailorShop(@PathVariable Integer id, @RequestBody @Valid TailorShop tailorShop, Errors errors) {
            tailorShopService.updateTailorShop(id, tailorShop);
            return ResponseEntity.status(200).body(new ApiResponse("Tailor shop updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTailorShop(@PathVariable Integer id) {
            tailorShopService.deleteTailorShop(id);
            return ResponseEntity.status(200).body(new ApiResponse("Tailor shop deleted successfully"));
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<?> getTailorShopsByCity(@PathVariable String city) {
        return ResponseEntity.status(200).body(tailorShopService.getTailorShopsByCity(city));
    }

    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<?> getTailorShopsBySpecialty(@PathVariable String specialty) {
        return ResponseEntity.status(200).body(tailorShopService.getTailorShopsBySpecialty(specialty));
    }
}

package com.example.maqas.Controller;

import com.example.maqas.Api.ApiException;
import com.example.maqas.Api.ApiResponse;
import com.example.maqas.Model.ShopOwner;
import com.example.maqas.Service.ShopOwnerService;
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
@RequestMapping("/api/v1/shop-owner")
@RequiredArgsConstructor
public class ShopOwnerController {

    private final ShopOwnerService shopOwnerService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllShopOwners() {
        return ResponseEntity.status(200).body(shopOwnerService.getShopOwners());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addShopOwner(@RequestBody @Valid ShopOwner shopOwner, Errors errors) {
            shopOwnerService.addShopOwner(shopOwner);
            return ResponseEntity.status(200).body(new ApiResponse("Shop owner added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateShopOwner(@PathVariable Integer id, @RequestBody @Valid ShopOwner shopOwner, Errors errors) {
            shopOwnerService.updateShopOwner(id, shopOwner);
            return ResponseEntity.status(200).body(new ApiResponse("Shop owner updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteShopOwner(@PathVariable Integer id) {
            shopOwnerService.deleteShopOwner(id);
            return ResponseEntity.status(200).body(new ApiResponse("Shop owner deleted successfully"));
    }
}

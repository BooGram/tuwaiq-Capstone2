package com.example.maqas.Controller;

import com.example.maqas.Api.ApiException;
import com.example.maqas.Api.ApiResponse;
import com.example.maqas.Model.ClothingOrder;
import com.example.maqas.Service.ClothingOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class ClothingOrderController {

    private final ClothingOrderService clothingOrderService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllClothingOrders() {
        return ResponseEntity.status(200).body(clothingOrderService.getClothingOrders());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createClothingOrder(@RequestBody @Valid ClothingOrder clothingOrder) {
            clothingOrderService.createClothingOrder(clothingOrder);
            return ResponseEntity.status(200).body(new ApiResponse("Clothing order created successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateClothingOrder(@PathVariable Integer id, @RequestBody @Valid ClothingOrder clothingOrder) {
            clothingOrderService.updateClothingOrder(id, clothingOrder);
            return ResponseEntity.status(200).body(new ApiResponse("Clothing order updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteClothingOrder(@PathVariable Integer id) {
            clothingOrderService.deleteClothingOrder(id);
            return ResponseEntity.status(200).body(new ApiResponse("Clothing order deleted successfully"));
    }

    @PutMapping("/change-status/{ownerId}/{orderId}/{status}")
    public ResponseEntity<?> changeOrderStatus(@PathVariable Integer ownerId, @PathVariable Integer orderId, @PathVariable String status) {
            clothingOrderService.changeOrderStatus(ownerId, orderId, status);
            return ResponseEntity.status(200).body(new ApiResponse("Order status changed successfully"));
    }

    @PutMapping("/quote/{ownerId}/{orderId}/{price}")
    public ResponseEntity<?> setOrderPrice(@PathVariable Integer ownerId, @PathVariable Integer orderId, @PathVariable Double price) {
            clothingOrderService.setOrderPrice(ownerId, orderId, price);
            return ResponseEntity.status(200).body(new ApiResponse("Order price quote sent successfully"));
    }

    @PutMapping("/accept-quote/{customerId}/{orderId}")
    public ResponseEntity<?> acceptPriceQuote(@PathVariable Integer customerId, @PathVariable Integer orderId) {
            clothingOrderService.acceptPriceQuote(customerId, orderId);
            return ResponseEntity.status(200).body(new ApiResponse("Order price quote accepted successfully"));
    }

    @PutMapping("/reject-quote/{customerId}/{orderId}")
    public ResponseEntity<?> rejectPriceQuote(@PathVariable Integer customerId, @PathVariable Integer orderId) {
            clothingOrderService.rejectPriceQuote(customerId, orderId);
            return ResponseEntity.status(200).body(new ApiResponse("Order price quote rejected successfully"));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getOrdersByCustomerId(@PathVariable Integer customerId) {
            return ResponseEntity.status(200).body(clothingOrderService.getOrdersByCustomerId(customerId));
    }

    @GetMapping("/shop/{tailorShopId}")
    public ResponseEntity<?> getOrdersByTailorShopId(@PathVariable Integer tailorShopId) {
            return ResponseEntity.status(200).body(clothingOrderService.getOrdersByTailorShopId(tailorShopId));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getOrdersByCategory(@PathVariable String category) {
            return ResponseEntity.status(200).body(clothingOrderService.getOrdersByCategory(category));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable String status) {
            return ResponseEntity.status(200).body(clothingOrderService.getOrdersByStatus(status));
    }

    @GetMapping("/ai/suggest/{category}")
    public ResponseEntity<?> getClothingSuggestions(@PathVariable String category) {
            return ResponseEntity.status(200).body(clothingOrderService.getClothingSuggestions(category));
    }
}

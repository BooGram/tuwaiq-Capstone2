package com.example.maqas.Controller;

import com.example.maqas.Api.ApiException;
import com.example.maqas.Api.ApiResponse;
import com.example.maqas.Model.ClothingOrder;
import com.example.maqas.Service.ClothingOrderService;
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
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class ClothingOrderController {

    private final ClothingOrderService clothingOrderService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllClothingOrders() {
        return ResponseEntity.status(200).body(clothingOrderService.getClothingOrders());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createClothingOrder(@RequestBody @Valid ClothingOrder clothingOrder, Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(message);
        }

        try {
            clothingOrderService.createClothingOrder(clothingOrder);
            return ResponseEntity.status(200).body(new ApiResponse("Clothing order created successfully"));
        } catch (ApiException e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateClothingOrder(@PathVariable Integer id, @RequestBody @Valid ClothingOrder clothingOrder, Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(message);
        }

        try {
            clothingOrderService.updateClothingOrder(id, clothingOrder);
            return ResponseEntity.status(200).body(new ApiResponse("Clothing order updated successfully"));
        } catch (ApiException e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteClothingOrder(@PathVariable Integer id) {
        try {
            clothingOrderService.deleteClothingOrder(id);
            return ResponseEntity.status(200).body(new ApiResponse("Clothing order deleted successfully"));
        } catch (ApiException e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }

    @PutMapping("/change-status/{orderId}/{status}")
    public ResponseEntity<?> changeOrderStatus(@PathVariable Integer orderId, @PathVariable String status) {
        try {
            clothingOrderService.changeOrderStatus(orderId, status);
            return ResponseEntity.status(200).body(new ApiResponse("Order status changed successfully"));
        } catch (ApiException e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }

    @PutMapping("/quote/{orderId}/{price}")
    public ResponseEntity<?> setOrderPrice(@PathVariable Integer orderId, @PathVariable Double price) {
        try {
            clothingOrderService.setOrderPrice(orderId, price);
            return ResponseEntity.status(200).body(new ApiResponse("Order price quote sent successfully"));
        } catch (ApiException e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }

    @PutMapping("/accept-quote/{orderId}")
    public ResponseEntity<?> acceptPriceQuote(@PathVariable Integer orderId) {
        try {
            clothingOrderService.acceptPriceQuote(orderId);
            return ResponseEntity.status(200).body(new ApiResponse("Order price quote accepted successfully"));
        } catch (ApiException e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }

    @PutMapping("/reject-quote/{orderId}")
    public ResponseEntity<?> rejectPriceQuote(@PathVariable Integer orderId) {
        try {
            clothingOrderService.rejectPriceQuote(orderId);
            return ResponseEntity.status(200).body(new ApiResponse("Order price quote rejected successfully"));
        } catch (ApiException e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getOrdersByCustomerId(@PathVariable Integer customerId) {
        try {
            return ResponseEntity.status(200).body(clothingOrderService.getOrdersByCustomerId(customerId));
        } catch (ApiException e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }

    @GetMapping("/shop/{tailorShopId}")
    public ResponseEntity<?> getOrdersByTailorShopId(@PathVariable Integer tailorShopId) {
        try {
            return ResponseEntity.status(200).body(clothingOrderService.getOrdersByTailorShopId(tailorShopId));
        } catch (ApiException e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getOrdersByCategory(@PathVariable String category) {
        try {
            return ResponseEntity.status(200).body(clothingOrderService.getOrdersByCategory(category));
        } catch (ApiException e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable String status) {
        try {
            return ResponseEntity.status(200).body(clothingOrderService.getOrdersByStatus(status));
        } catch (ApiException e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }

    @GetMapping("/ai/suggest/{category}")
    public ResponseEntity<?> getClothingSuggestions(@PathVariable String category) {
        try {
            return ResponseEntity.status(200).body(clothingOrderService.getClothingSuggestions(category));
        } catch (ApiException e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }
}

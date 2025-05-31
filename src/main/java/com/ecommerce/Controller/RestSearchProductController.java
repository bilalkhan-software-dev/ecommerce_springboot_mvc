package com.ecommerce.Controller;

import com.ecommerce.Entity.Product;
import com.ecommerce.Repository.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RestSearchProductController {

    private final ProductRepo productRepo;

    @GetMapping("/suggestion")
    public ResponseEntity<List<Product>> searchProductSuggestion(@RequestParam("search") String query) {
        List<Product> searchResult = productRepo.findByTitleContainingAndIsActiveTrue(query);
        return ResponseEntity.ok(searchResult);
    }
}

package org.example.backend.controller;

import org.example.backend.dto.ProductDTO;
import org.example.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/gift-suggestions")
public class GiftSuggestionController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getGiftSuggestions(
            @RequestParam(required = false) String recipient,
            @RequestParam(required = false) String occasion,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "rating") String sort // rating, price_asc, price_desc
    ) {
        Sort sortObj = Sort.by(Sort.Direction.DESC, "rating");
        if ("price_asc".equals(sort)) {
            sortObj = Sort.by(Sort.Direction.ASC, "basePrice");
        } else if ("price_desc".equals(sort)) {
            sortObj = Sort.by(Sort.Direction.DESC, "basePrice");
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);
        return ResponseEntity.ok(productService.getGiftSuggestions(recipient, occasion, minPrice, maxPrice, pageable));
    }
}

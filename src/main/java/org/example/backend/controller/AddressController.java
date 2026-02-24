package org.example.backend.controller;

import org.example.backend.dto.AddressDTO;
import org.example.backend.model.entity.User;
import org.example.backend.service.AddressService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private org.example.backend.repository.UserRepository userRepository;

    private Long getUserIdWrapped(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getUserId();
    }

    @GetMapping
    public ResponseEntity<List<AddressDTO>> getUserAddresses(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserIdWrapped(userDetails);
        return ResponseEntity.ok(addressService.getUserAddresses(userId));
    }

    @GetMapping("/default")
    public ResponseEntity<AddressDTO> getDefaultAddress(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserIdWrapped(userDetails);
        return ResponseEntity.ok(addressService.getDefaultAddress(userId));
    }

    @PostMapping
    public ResponseEntity<AddressDTO> addAddress(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AddressDTO addressDTO) {
        Long userId = getUserIdWrapped(userDetails);
        return ResponseEntity.ok(addressService.addAddress(userId, addressDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressDTO> updateAddress(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id, @RequestBody AddressDTO addressDTO) {
        Long userId = getUserIdWrapped(userDetails);
        return ResponseEntity.ok(addressService.updateAddress(userId, id, addressDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        Long userId = getUserIdWrapped(userDetails);
        addressService.deleteAddress(userId, id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/default")
    public ResponseEntity<Void> setDefaultAddress(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        Long userId = getUserIdWrapped(userDetails);
        addressService.setDefaultAddress(userId, id);
        return ResponseEntity.ok().build();
    }
}

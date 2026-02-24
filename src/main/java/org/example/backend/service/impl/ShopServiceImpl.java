package org.example.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.ShopRegisterRequest;
import org.example.backend.dto.ShopResponse;
import org.example.backend.dto.ShopUpdateRequest;
import org.example.backend.model.entity.Address;
import org.example.backend.model.entity.Shop;
import org.example.backend.model.entity.User;
import org.example.backend.repository.AddressRepository;
import org.example.backend.repository.ShopRepository;
import org.example.backend.repository.UserRepository;
import org.example.backend.service.ShopService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    @Override
    @Transactional
    public ShopResponse registerShop(ShopRegisterRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (shopRepository.findByOwner(user).isPresent()) {
            throw new RuntimeException("Tài khoản này đã đăng ký Shop");
        }

        if (shopRepository.existsByShopName(request.getShopName())) {
            throw new RuntimeException("Tên Shop đã tồn tại, vui lòng chọn tên khác");
        }

        String slug = toSlug(request.getShopName());
        if (shopRepository.findByShopSlug(slug).isPresent()) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        Shop shop = Shop.builder()
                .shopName(request.getShopName())
                .shopSlug(slug)
                .owner(user)
                .status(Shop.ShopStatus.ACTIVE)
                .description("Shop mới tạo")
                .taxCode(request.getTaxCode())
                .identityNumber(request.getIdentityNumber())
                .build();

        shop = shopRepository.save(shop);

        Address address = Address.builder()
                .user(user)
                .recipientName(request.getShopName())
                .recipientPhone(request.getPhoneNumber())
                .city(request.getCity())
                .district(request.getDistrict())
                .ward(request.getWard())
                .addressLine1(request.getAddressDetail())
                .type(Address.AddressType.PICKUP)
                .isDefault(false)
                .build();

        addressRepository.save(address);

        return ShopResponse.fromEntity(shop);
    }

    @Override
    public ShopResponse getCurrentShop(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return shopRepository.findByOwner(user)
                .map(shop -> {
                    ShopResponse response = ShopResponse.fromEntity(shop);
                    setPickupAddress(response, user);
                    return response;
                })
                .orElse(null);
    }

    @Override
    public ShopResponse getShopById(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + shopId));
        ShopResponse response = ShopResponse.fromEntity(shop);
        setPickupAddress(response, shop.getOwner());
        return response;
    }

    private void setPickupAddress(ShopResponse response, User owner) {
        addressRepository.findByUser(owner).stream()
                .filter(a -> a.getType() == Address.AddressType.PICKUP)
                .findFirst()
                .ifPresent(address -> {
                    String fullAddress = (address.getAddressLine1() != null ? address.getAddressLine1() : "") + ", " +
                            (address.getWard() != null ? address.getWard() : "") + ", " +
                            (address.getDistrict() != null ? address.getDistrict() : "") + ", " +
                            (address.getCity() != null ? address.getCity() : "");
                    // Simple cleanup
                    fullAddress = fullAddress.replace(", ,", ",").replaceAll("^, ", "").replaceAll(", $", "");
                    response.setPickupAddress(fullAddress);
                });
    }

    @Override
    @Transactional
    public ShopResponse updateShop(ShopUpdateRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Shop shop = shopRepository.findByOwner(user)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        if (request.getShopName() != null) {
            shop.setShopName(request.getShopName());
        }
        if (request.getDescription() != null) {
            shop.setDescription(request.getDescription());
        }
        if (request.getLogoUrl() != null) {
            shop.setLogoUrl(request.getLogoUrl());
        }
        if (request.getBannerUrl() != null) {
            shop.setBannerUrl(request.getBannerUrl());
        }
        if (request.getShippingPolicy() != null) {
            shop.setShippingPolicy(request.getShippingPolicy());
        }
        if (request.getReturnPolicy() != null) {
            shop.setReturnPolicy(request.getReturnPolicy());
        }

        shopRepository.save(shop);

        // Update Address if any address field is present
        if (request.getAddressDetail() != null || request.getCity() != null) {
            java.util.List<Address> addresses = addressRepository.findByUser(user);
            Address pickupAddress = addresses.stream()
                    .filter(a -> a.getType() == Address.AddressType.PICKUP)
                    .findFirst()
                    .orElse(null);

            if (pickupAddress != null) {
                if (request.getAddressDetail() != null)
                    pickupAddress.setAddressLine1(request.getAddressDetail());
                if (request.getCity() != null)
                    pickupAddress.setCity(request.getCity());
                if (request.getDistrict() != null)
                    pickupAddress.setDistrict(request.getDistrict());
                if (request.getWard() != null)
                    pickupAddress.setWard(request.getWard());
                addressRepository.save(pickupAddress);
            } else {
                // Create new if not exists
                Address newAddress = Address.builder()
                        .user(user)
                        .addressLine1(request.getAddressDetail())
                        .city(request.getCity())
                        .district(request.getDistrict())
                        .ward(request.getWard())
                        .type(Address.AddressType.PICKUP)
                        .recipientName(shop.getShopName())
                        .recipientPhone(user.getPhoneNumber()) // Fallback
                        .isDefault(false)
                        .build();
                addressRepository.save(newAddress);
            }
        }

        ShopResponse response = ShopResponse.fromEntity(shop);
        setPickupAddress(response, user);
        return response;
    }

    public String toSlug(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }
}

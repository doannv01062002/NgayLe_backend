package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.model.entity.Shop;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopResponse {
    private Long shopId;
    private String shopName;
    private String shopSlug;
    private String logoUrl;
    private String bannerUrl;
    private Shop.ShopStatus status;
    private Long ownerId;
    private String pickupAddress;
    private String shippingPolicy;
    private String returnPolicy;

    public static ShopResponse fromEntity(Shop shop) {
        return ShopResponse.builder()
                .shopId(shop.getShopId())
                .shopName(shop.getShopName())
                .shopSlug(shop.getShopSlug())
                .logoUrl(shop.getLogoUrl())
                .bannerUrl(shop.getBannerUrl())
                .status(shop.getStatus())
                .ownerId(shop.getOwner().getUserId())
                .pickupAddress(shop.getPickupAddress())
                .shippingPolicy(shop.getShippingPolicy())
                .returnPolicy(shop.getReturnPolicy())
                .build();
    }
}

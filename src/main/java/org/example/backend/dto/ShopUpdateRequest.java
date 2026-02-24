package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopUpdateRequest {
    private String shopName;
    private String description;
    private String logoUrl;
    private String bannerUrl;
    // Address fields could be added here if we want to update address
    // simultaneously
    private String city;
    private String district;
    private String ward;
    private String addressDetail;
    private String shippingPolicy;
    private String returnPolicy;
}

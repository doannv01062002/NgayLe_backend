package org.example.backend.dto;

import lombok.Data;

@Data
public class ShopRegisterRequest {
    private String shopName;
    private String email;
    private String phoneNumber;
    private String city;
    private String district;
    private String ward;
    private String addressDetail;
    private String taxCode;
    private String identityNumber;
}

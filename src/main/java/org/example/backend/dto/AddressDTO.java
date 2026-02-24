package org.example.backend.dto;

import lombok.Data;

@Data
public class AddressDTO {
    private Long addressId;
    private String recipientName;
    private String recipientPhone;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String district;
    private String ward;
    private Boolean isDefault;
    private String type; // DELIVERY, PICKUP, RETURN
}

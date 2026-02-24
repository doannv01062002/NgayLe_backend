package org.example.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {
    private Long addressId;
    private String shippingMethod; // "nhanh", "hoatoc"
    private String paymentMethod; // "cod", "momo", "card"
    private List<Long> cartItemIds;
    private String voucherCode;
}

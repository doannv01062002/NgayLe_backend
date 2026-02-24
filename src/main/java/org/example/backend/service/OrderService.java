package org.example.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.backend.dto.AddressDTO;
import org.example.backend.dto.CreateOrderRequest;
import org.example.backend.dto.OrderDTO;
import org.example.backend.model.entity.*;
import org.example.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Transactional
    public List<OrderDTO> createOrder(Long userId, CreateOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Invalid address");
        }

        // Fetch Cart Items
        List<CartItem> cartItems = cartItemRepository.findAllById(request.getCartItemIds());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("No items selected");
        }

        // Validate items belong to user
        for (CartItem item : cartItems) {
            if (!item.getCart().getUser().getUserId().equals(userId)) {
                throw new RuntimeException("Invalid cart item");
            }
        }

        // Group by Shop
        Map<Shop, List<CartItem>> itemsByShop = cartItems.stream()
                .collect(Collectors.groupingBy(item -> item.getProductVariant().getProduct().getShop()));

        List<Order> createdOrders = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        // Create simplified map/DTO for address to avoid recursion/lazy issues
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setAddressId(address.getAddressId());
        addressDTO.setRecipientName(address.getRecipientName());
        addressDTO.setRecipientPhone(address.getRecipientPhone());
        addressDTO.setAddressLine1(address.getAddressLine1());
        addressDTO.setAddressLine2(address.getAddressLine2());
        addressDTO.setCity(address.getCity());
        addressDTO.setDistrict(address.getDistrict());
        addressDTO.setWard(address.getWard());
        addressDTO.setIsDefault(address.getIsDefault());
        addressDTO.setType(address.getType() != null ? address.getType().name() : "DELIVERY");

        String addressJson;
        try {
            addressJson = mapper.writeValueAsString(addressDTO);
        } catch (Exception e) {
            // Fallback to simple string
            addressJson = address.getAddressLine1() + ", " + address.getCity();
        }

        for (Map.Entry<Shop, List<CartItem>> entry : itemsByShop.entrySet()) {
            Shop shop = entry.getKey();
            List<CartItem> shopItems = entry.getValue();

            Order order = new Order();
            order.setUser(user);
            order.setShop(shop);
            order.setStatus(Order.OrderStatus.PENDING_PAYMENT);
            order.setPaymentStatus(Order.PaymentStatus.UNPAID);

            try {
                order.setPaymentMethod(Order.PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase()));
            } catch (Exception e) {
                order.setPaymentMethod(Order.PaymentMethod.COD);
            }

            order.setShippingAddressJson(addressJson);
            order.setShippingCarrier(request.getShippingMethod()); // "nhanh" or "hoatoc"

            // Calculate Totals
            BigDecimal totalAmount = BigDecimal.ZERO;
            List<OrderItem> orderItems = new ArrayList<>();

            for (CartItem cartItem : shopItems) {
                ProductVariant variant = cartItem.getProductVariant();

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProductVariant(variant);
                orderItem.setProduct(variant.getProduct());
                orderItem.setProductName(variant.getProduct().getName());

                String vName = variant.getName();
                if (vName == null || vName.isEmpty()) {
                    vName = (variant.getOption1Value() != null ? variant.getOption1Value() : "") +
                            (variant.getOption2Value() != null ? " " + variant.getOption2Value() : "");
                }
                orderItem.setVariantName(vName.trim().isEmpty() ? "Default" : vName.trim());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setUnitPrice(variant.getPrice());

                BigDecimal lineTotal = variant.getPrice().multiply(new BigDecimal(cartItem.getQuantity()));
                orderItem.setTotalPrice(lineTotal);

                totalAmount = totalAmount.add(lineTotal);
                orderItems.add(orderItem);
            }

            order.setOrderItems(orderItems);
            order.setTotalAmount(totalAmount);

            // Shipping Fee Logic (Mock)
            BigDecimal shippingFee = "hoatoc".equalsIgnoreCase(request.getShippingMethod())
                    ? new BigDecimal("55000")
                    : new BigDecimal("30000");
            order.setShippingFee(shippingFee);

            // Discount Logic (Mock)
            BigDecimal discount = BigDecimal.ZERO;
            // if (request.getVoucherCode() ...)

            order.setDiscountAmount(discount);
            order.setFinalAmount(totalAmount.add(shippingFee).subtract(discount));

            System.out.println("Saving order for shop: " + shop.getShopName());
            try {
                Order savedOrder = orderRepository.save(order);
                createdOrders.add(savedOrder);
            } catch (Exception e) {
                System.err.println("Error saving order: " + e.getMessage());
                e.printStackTrace();
                throw e; // Rethrow to rollback transaction
            }

            // Remove from cart
            cartItemRepository.deleteAll(shopItems);
        }

        System.out.println("Converting to DTOs...");
        return createdOrders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAllByOrderByCreatedAtDesc();
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public org.example.backend.dto.StatsDTO getAdminOrderStats() {
        org.example.backend.dto.StatsDTO stats = new org.example.backend.dto.StatsDTO();

        stats.setTotal(orderRepository.count());

        stats.setPending(orderRepository.count((org.springframework.data.jpa.domain.Specification<Order>) (root, query,
                cb) -> cb.equal(root.get("status"), Order.OrderStatus.PENDING_PAYMENT))); // Or PROCESSING

        stats.setActive(orderRepository.count((org.springframework.data.jpa.domain.Specification<Order>) (root, query,
                cb) -> cb.equal(root.get("status"), Order.OrderStatus.COMPLETED)));

        stats.setBanned(orderRepository.count((org.springframework.data.jpa.domain.Specification<Order>) (root, query,
                cb) -> cb.equal(root.get("status"), Order.OrderStatus.CANCELLED)));

        // Use 'Reported' field for RETURNED orders maybe?
        stats.setReported(orderRepository.count((org.springframework.data.jpa.domain.Specification<Order>) (root, query,
                cb) -> cb.equal(root.get("status"), Order.OrderStatus.RETURNED)));

        return stats;
    }

    @Transactional
    public OrderDTO updateStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        try {
            order.setStatus(Order.OrderStatus.valueOf(status));
            orderRepository.save(order);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }
        return convertToDTO(order);
    }

    public OrderDTO getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        return convertToDTO(order);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setShopId(order.getShop().getShopId());
        dto.setShopName(order.getShop().getShopName());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setShippingFee(order.getShippingFee());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setFinalAmount(order.getFinalAmount());
        dto.setStatus(order.getStatus().name());
        dto.setPaymentStatus(order.getPaymentStatus().name());
        dto.setPaymentMethod(order.getPaymentMethod().name());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setCustomerName(order.getUser().getFullName());

        if (order.getOrderItems() != null) {
            List<org.example.backend.dto.OrderItemDTO> items = order.getOrderItems().stream().map(item -> {
                org.example.backend.dto.OrderItemDTO idto = new org.example.backend.dto.OrderItemDTO();
                idto.setItemId(item.getItemId());
                idto.setProductId(item.getProduct().getProductId());
                idto.setProductName(item.getProductName());
                idto.setVariantName(item.getVariantName());
                idto.setQuantity(item.getQuantity());
                idto.setUnitPrice(item.getUnitPrice());
                idto.setTotalPrice(item.getTotalPrice());
                if (item.getProductVariant() != null) {
                    idto.setImageUrl(item.getProductVariant().getImageUrl());
                }
                return idto;
            }).collect(Collectors.toList());
            dto.setItems(items);
        }
        return dto;
    }
}

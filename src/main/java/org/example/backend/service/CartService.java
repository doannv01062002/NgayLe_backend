package org.example.backend.service;

import org.example.backend.dto.CartDTO;
import org.example.backend.dto.CartItemDTO;
import org.example.backend.model.entity.Cart;
import org.example.backend.model.entity.CartItem;
import org.example.backend.model.entity.ProductVariant;
import org.example.backend.model.entity.User;
import org.example.backend.repository.CartItemRepository;
import org.example.backend.repository.CartRepository;
import org.example.backend.repository.ProductVariantRepository;
import org.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductVariantRepository productVariantRepository;
    @Autowired
    private UserRepository userRepository;

    public CartDTO getCart(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        return convertToDTO(cart);
    }

    @Transactional
    public CartDTO addToCart(Long userId, Long variantId, Integer quantity) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Product variant not found"));

        // Check if item already exists
        Optional<CartItem> existingItem = cart.getCartItems() != null ? cart.getCartItems().stream()
                .filter(item -> item.getProductVariant().getVariantId().equals(variantId))
                .findFirst() : Optional.empty();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProductVariant(variant);
            newItem.setQuantity(quantity);
            newItem.setSelected(true);

            if (cart.getCartItems() == null) {
                cart.setCartItems(new ArrayList<>());
            }
            cart.getCartItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        return getCart(userId);
    }

    @Transactional
    public CartDTO updateQuantity(Long userId, Long itemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getCart().getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
        return getCart(userId);
    }

    @Transactional
    public CartDTO removeFromCart(Long userId, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getCart().getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        if (item.getCart().getCartItems() != null) {
            item.getCart().getCartItems().remove(item);
        }
        cartItemRepository.delete(item);
        cartItemRepository.flush(); // Force delete execution

        // Refresh cart to ensure we have strict consistency or just rely on memory
        // update above
        // Since we are in the same transaction, getCart might just return the memory
        // object, which we fixed above.
        return getCart(userId);
    }

    private CartDTO convertToDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setCartId(cart.getCartId());
        dto.setUserId(cart.getUser().getUserId());

        List<CartItemDTO> itemDTOs = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalItems = 0;

        if (cart.getCartItems() != null) {
            for (CartItem item : cart.getCartItems()) {
                CartItemDTO itemDTO = new CartItemDTO();
                itemDTO.setItemId(item.getItemId());
                itemDTO.setProductId(item.getProductVariant().getProduct().getProductId());
                itemDTO.setProductName(item.getProductVariant().getProduct().getName());
                itemDTO.setVariantId(item.getProductVariant().getVariantId());
                itemDTO.setVariantName(item.getProductVariant().getName());

                // Use variant image or product image
                String imageUrl = item.getProductVariant().getImageUrl();
                if (imageUrl == null || imageUrl.isEmpty()) {
                    if (item.getProductVariant().getProduct().getImages() != null
                            && !item.getProductVariant().getProduct().getImages().isEmpty()) {
                        imageUrl = item.getProductVariant().getProduct().getImages().get(0).getImageUrl();
                    }
                }
                itemDTO.setImageUrl(imageUrl);

                itemDTO.setPrice(item.getProductVariant().getPrice());
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setSelected(item.getSelected());

                BigDecimal subtotal = item.getProductVariant().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()));
                itemDTO.setSubtotal(subtotal);

                if (Boolean.TRUE.equals(item.getSelected())) {
                    totalAmount = totalAmount.add(subtotal);
                    totalItems += item.getQuantity();
                }

                itemDTOs.add(itemDTO);
            }
        }

        dto.setItems(itemDTOs);
        dto.setTotalAmount(totalAmount);
        dto.setTotalItems(totalItems);
        return dto;
    }
}

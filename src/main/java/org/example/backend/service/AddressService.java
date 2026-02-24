package org.example.backend.service;

import org.example.backend.dto.AddressDTO;
import org.example.backend.model.entity.Address;
import org.example.backend.model.entity.User;
import org.example.backend.repository.AddressRepository;
import org.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    public List<AddressDTO> getUserAddresses(Long userId) {
        if (userId == null)
            throw new IllegalArgumentException("User ID cannot be null");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return addressRepository.findByUser(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AddressDTO getDefaultAddress(Long userId) {
        if (userId == null)
            throw new IllegalArgumentException("User ID cannot be null");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Address address = addressRepository.findByUserAndIsDefaultTrue(user);
        return address != null ? convertToDTO(address) : null;
    }

    @Transactional
    public AddressDTO addAddress(Long userId, AddressDTO addressDTO) {
        if (userId == null)
            throw new IllegalArgumentException("User ID cannot be null");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // If this is the first address, make it default
        boolean isFirst = addressRepository.findByUser(user).isEmpty();
        if (isFirst) {
            addressDTO.setIsDefault(true);
        } else if (Boolean.TRUE.equals(addressDTO.getIsDefault())) {
            // detailed logic to unset other defaults handled in 'setDefaultAddress'
            // generally,
            // but here we just need to ensure consistency if we add a NEW default.
            unsetOtherDefaults(user);
        }

        Address address = new Address();
        address.setUser(user);
        updateEntityFromDTO(address, addressDTO);

        Address savedAddress = addressRepository.save(address);
        return convertToDTO(savedAddress);
    }

    @Transactional
    public AddressDTO updateAddress(Long userId, Long addressId, AddressDTO addressDTO) {
        if (userId == null || addressId == null)
            throw new IllegalArgumentException("IDs cannot be null");
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to address");
        }

        if (Boolean.TRUE.equals(addressDTO.getIsDefault()) && !Boolean.TRUE.equals(address.getIsDefault())) {
            unsetOtherDefaults(address.getUser());
        }

        updateEntityFromDTO(address, addressDTO);
        Address savedAddress = addressRepository.save(address);
        return convertToDTO(savedAddress);
    }

    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        if (userId == null || addressId == null)
            throw new IllegalArgumentException("IDs cannot be null");
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to address");
        }

        addressRepository.delete(address);
    }

    @Transactional
    public void setDefaultAddress(Long userId, Long addressId) {
        if (userId == null || addressId == null)
            throw new IllegalArgumentException("IDs cannot be null");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to address");
        }

        unsetOtherDefaults(user);
        address.setIsDefault(true);
        addressRepository.save(address);
    }

    private void unsetOtherDefaults(User user) {
        List<Address> addresses = addressRepository.findByUser(user);
        for (Address addr : addresses) {
            if (Boolean.TRUE.equals(addr.getIsDefault())) {
                addr.setIsDefault(false);
                addressRepository.save(addr);
            }
        }
    }

    private AddressDTO convertToDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        dto.setAddressId(address.getAddressId());
        dto.setRecipientName(address.getRecipientName());
        dto.setRecipientPhone(address.getRecipientPhone());
        dto.setAddressLine1(address.getAddressLine1());
        dto.setAddressLine2(address.getAddressLine2());
        dto.setCity(address.getCity());
        dto.setDistrict(address.getDistrict());
        dto.setWard(address.getWard());
        dto.setIsDefault(address.getIsDefault());
        dto.setType(address.getType() != null ? address.getType().name() : "DELIVERY");
        return dto;
    }

    private void updateEntityFromDTO(Address address, AddressDTO dto) {
        address.setRecipientName(dto.getRecipientName());
        address.setRecipientPhone(dto.getRecipientPhone());
        address.setAddressLine1(dto.getAddressLine1());
        address.setAddressLine2(dto.getAddressLine2());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setWard(dto.getWard());
        if (dto.getIsDefault() != null) {
            address.setIsDefault(dto.getIsDefault());
        }
        if (dto.getType() != null) {
            try {
                address.setType(Address.AddressType.valueOf(dto.getType()));
            } catch (IllegalArgumentException e) {
                address.setType(Address.AddressType.DELIVERY);
            }
        }
    }
}

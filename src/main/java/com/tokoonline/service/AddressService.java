package com.tokoonline.service;

import com.tokoonline.dto.request.AddressRequest;
import com.tokoonline.dto.response.AddressResponse;
import com.tokoonline.entity.Address;
import com.tokoonline.entity.User;
import com.tokoonline.exception.ResourceNotFoundException;
import com.tokoonline.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    public List<AddressResponse> getUserAddresses(User user) {
        return addressRepository.findByUserId(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AddressResponse createAddress(User user, AddressRequest request) {
        Address address = Address.builder()
                .user(user)
                .label(request.getLabel())
                .fullAddress(request.getFullAddress())
                .city(request.getCity())
                .postalCode(request.getPostalCode())
                .phone(request.getPhone())
                .build();

        address = addressRepository.save(address);
        return mapToResponse(address);
    }

    public AddressResponse updateAddress(Long id, User user, AddressRequest request) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alamat", id));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Alamat", id);
        }

        address.setLabel(request.getLabel());
        address.setFullAddress(request.getFullAddress());
        address.setCity(request.getCity());
        address.setPostalCode(request.getPostalCode());
        address.setPhone(request.getPhone());

        address = addressRepository.save(address);
        return mapToResponse(address);
    }

    public void deleteAddress(Long id, User user) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alamat", id));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Alamat", id);
        }

        addressRepository.delete(address);
    }

    private AddressResponse mapToResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .label(address.getLabel())
                .fullAddress(address.getFullAddress())
                .city(address.getCity())
                .postalCode(address.getPostalCode())
                .phone(address.getPhone())
                .build();
    }
}
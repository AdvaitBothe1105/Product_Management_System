package com.jforce.interview2.service;

import com.jforce.interview2.dto.AddressRequest;
import com.jforce.interview2.dto.AddressResponse;
import com.jforce.interview2.model.Address;
import com.jforce.interview2.model.User;
import com.jforce.interview2.repo.AddressRepo;
import com.jforce.interview2.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepo addressRepo;
    private final UserRepo userRepo;

    private User getLoggedInUser(){
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    private AddressResponse toDTO(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getPincode(),
                address.getCountry(),
                address.isDefault()
        );
    }

    public List<AddressResponse> getMyAddresses(){
        User user = getLoggedInUser();
        return addressRepo.findByUserId(user.getId())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public AddressResponse addAddress(AddressRequest request) {
        User user = getLoggedInUser();

        // if this is set as default
        // remove default from all existing addresses first
        if (request.isDefault()) {
            List<Address> existing = addressRepo.findByUserId(user.getId());
            existing.forEach(a -> a.setDefault(false));
            addressRepo.saveAll(existing);
        }

        Address address = Address.builder()
                .user(user)
                .street(request.street())
                .city(request.city())
                .state(request.state())
                .pincode(request.pincode())
                .country(request.country())
                .isDefault(request.isDefault())
                .build();

        return toDTO(addressRepo.save(address));
    }

    @Transactional
    public AddressResponse updateAddress(Integer id, AddressRequest request) {
        User user = getLoggedInUser();

        Address address = addressRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Address not found: " + id));

        // make sure address belongs to logged in user
        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        // handle default switching
        if (request.isDefault() && !address.isDefault()) {
            List<Address> existing = addressRepo.findByUserId(user.getId());
            existing.forEach(a -> a.setDefault(false));
            addressRepo.saveAll(existing);
        }

        address.setStreet(request.street());
        address.setCity(request.city());
        address.setState(request.state());
        address.setPincode(request.pincode());
        address.setCountry(request.country());
        address.setDefault(request.isDefault());

        return toDTO(addressRepo.save(address));
    }

    public void deleteAddress(Integer id) {
        User user = getLoggedInUser();

        Address address = addressRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Address not found: " + id));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        addressRepo.delete(address);
    }
}

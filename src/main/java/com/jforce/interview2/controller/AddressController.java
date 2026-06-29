package com.jforce.interview2.controller;

import com.jforce.interview2.dto.AddressRequest;
import com.jforce.interview2.dto.AddressResponse;
import com.jforce.interview2.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @GetMapping
    public List<AddressResponse> getMyAddresses() {
        return addressService.getMyAddresses();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddressResponse addAddress(
            @Valid @RequestBody AddressRequest request) {
        return addressService.addAddress(request);
    }

    @PutMapping("/{id}")
    public AddressResponse updateAddress(
            @PathVariable Integer id,
            @Valid @RequestBody AddressRequest request) {
        return addressService.updateAddress(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(@PathVariable Integer id) {
        addressService.deleteAddress(id);
    }
}

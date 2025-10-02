package com.ajit.shopsphere.services;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.ajit.shopsphere.auth.entities.User;
import com.ajit.shopsphere.dtos.AddressRequest;
import com.ajit.shopsphere.entities.Address;
import com.ajit.shopsphere.repositories.AddressRepository;

@Service
public class AddressService {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AddressRepository addressRepository;
    
    public Address createAddress(AddressRequest addressRequest, Principal principal){
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Address address = Address.builder()
                .name(addressRequest.getName())
                .street(addressRequest.getStreet())
                .city(addressRequest.getCity())
                .state(addressRequest.getState())
                .zipCode(addressRequest.getZipCode())
                .phoneNumber(addressRequest.getPhoneNumber())
                .user(user)
                .build();

        return addressRepository.save(address);
    }
}

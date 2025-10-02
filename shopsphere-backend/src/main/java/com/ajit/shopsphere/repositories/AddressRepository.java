package com.ajit.shopsphere.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ajit.shopsphere.entities.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    
}

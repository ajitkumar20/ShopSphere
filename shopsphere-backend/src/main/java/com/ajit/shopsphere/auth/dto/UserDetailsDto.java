package com.ajit.shopsphere.auth.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailsDto {
    
    private UUID id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private Object authorityList;
}

package com.tpa.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CarrierRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Mobile number is required")
    private String mobile;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Company type is required")
    private String companyType;

    @NotBlank(message = "Registration number is required")
    private String registrationNumber;

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    @NotBlank(message = "Tax ID / GSTIN is required")
    private String taxId;

    private String website;
}

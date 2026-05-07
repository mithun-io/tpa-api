package com.tpa.dto.response;

import com.tpa.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrierResponse {

    private Long id;

    private String companyName;

    private String email;

    private String mobile;

    private String companyType;

    private String licenseNumber;

    private String registrationNumber;

    private String taxId;

    private String contactPersonName;

    private String contactPersonPhone;

    private String website;

    private UserStatus userStatus;

    private Double aiRiskScore;

    private String aiRiskStatus;

    private String aiRecommendation;
}

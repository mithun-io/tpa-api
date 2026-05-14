
package com.tpa.dto.response;

import com.tpa.enums.Gender;
import com.tpa.enums.UserRole;
import com.tpa.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PatientResponse {

    private Long id;

    private String name;

    private String email;

    private String phoneNumber;

    private LocalDate dateOfBirth;

    private String address;

    private Gender gender;

    private UserRole patientRole;

    private UserStatus patientStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm a")
    private LocalDateTime createdAt;
}
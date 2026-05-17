package com.tpa.mapper;

import com.tpa.dto.request.user.PatientRequest;
import com.tpa.dto.response.user.PatientResponse;
import com.tpa.entity.Patient;
import com.tpa.entity.User;
import com.tpa.enums.Gender;
import com.tpa.enums.UserRole;
import com.tpa.enums.UserStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-17T09:13:19+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class PatientMapperImpl implements PatientMapper {

    @Override
    public Patient toPatient(PatientRequest patientRequest) {
        if ( patientRequest == null ) {
            return null;
        }

        Patient.PatientBuilder patient = Patient.builder();

        patient.user( patientRequestToUser( patientRequest ) );

        return patient.build();
    }

    @Override
    public PatientResponse toPatientResponse(Patient patient) {
        if ( patient == null ) {
            return null;
        }

        PatientResponse patientResponse = new PatientResponse();

        patientResponse.setName( patientUserUsername( patient ) );
        patientResponse.setEmail( patientUserEmail( patient ) );
        patientResponse.setPhoneNumber( patientUserPhoneNumber( patient ) );
        patientResponse.setDateOfBirth( patientUserDateOfBirth( patient ) );
        patientResponse.setAddress( patientUserAddress( patient ) );
        patientResponse.setGender( patientUserGender( patient ) );
        patientResponse.setPatientRole( patientUserUserRole( patient ) );
        patientResponse.setPatientStatus( patientUserUserStatus( patient ) );
        patientResponse.setCreatedAt( patientUserCreatedAt( patient ) );
        patientResponse.setId( patient.getId() );

        return patientResponse;
    }

    @Override
    public List<PatientResponse> toPatientResponses(List<Patient> patients) {
        if ( patients == null ) {
            return null;
        }

        List<PatientResponse> list = new ArrayList<PatientResponse>( patients.size() );
        for ( Patient patient : patients ) {
            list.add( toPatientResponse( patient ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDto(PatientRequest patientRequest, Patient patient) {
        if ( patientRequest == null ) {
            return;
        }
    }

    protected User patientRequestToUser(PatientRequest patientRequest) {
        if ( patientRequest == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.username( patientRequest.getName() );
        user.email( patientRequest.getEmail() );
        user.phoneNumber( patientRequest.getPhoneNumber() );
        user.dateOfBirth( patientRequest.getDateOfBirth() );
        user.address( patientRequest.getAddress() );
        user.gender( patientRequest.getGender() );

        return user.build();
    }

    private String patientUserUsername(Patient patient) {
        if ( patient == null ) {
            return null;
        }
        User user = patient.getUser();
        if ( user == null ) {
            return null;
        }
        String username = user.getUsername();
        if ( username == null ) {
            return null;
        }
        return username;
    }

    private String patientUserEmail(Patient patient) {
        if ( patient == null ) {
            return null;
        }
        User user = patient.getUser();
        if ( user == null ) {
            return null;
        }
        String email = user.getEmail();
        if ( email == null ) {
            return null;
        }
        return email;
    }

    private String patientUserPhoneNumber(Patient patient) {
        if ( patient == null ) {
            return null;
        }
        User user = patient.getUser();
        if ( user == null ) {
            return null;
        }
        String phoneNumber = user.getPhoneNumber();
        if ( phoneNumber == null ) {
            return null;
        }
        return phoneNumber;
    }

    private LocalDate patientUserDateOfBirth(Patient patient) {
        if ( patient == null ) {
            return null;
        }
        User user = patient.getUser();
        if ( user == null ) {
            return null;
        }
        LocalDate dateOfBirth = user.getDateOfBirth();
        if ( dateOfBirth == null ) {
            return null;
        }
        return dateOfBirth;
    }

    private String patientUserAddress(Patient patient) {
        if ( patient == null ) {
            return null;
        }
        User user = patient.getUser();
        if ( user == null ) {
            return null;
        }
        String address = user.getAddress();
        if ( address == null ) {
            return null;
        }
        return address;
    }

    private Gender patientUserGender(Patient patient) {
        if ( patient == null ) {
            return null;
        }
        User user = patient.getUser();
        if ( user == null ) {
            return null;
        }
        Gender gender = user.getGender();
        if ( gender == null ) {
            return null;
        }
        return gender;
    }

    private UserRole patientUserUserRole(Patient patient) {
        if ( patient == null ) {
            return null;
        }
        User user = patient.getUser();
        if ( user == null ) {
            return null;
        }
        UserRole userRole = user.getUserRole();
        if ( userRole == null ) {
            return null;
        }
        return userRole;
    }

    private UserStatus patientUserUserStatus(Patient patient) {
        if ( patient == null ) {
            return null;
        }
        User user = patient.getUser();
        if ( user == null ) {
            return null;
        }
        UserStatus userStatus = user.getUserStatus();
        if ( userStatus == null ) {
            return null;
        }
        return userStatus;
    }

    private LocalDateTime patientUserCreatedAt(Patient patient) {
        if ( patient == null ) {
            return null;
        }
        User user = patient.getUser();
        if ( user == null ) {
            return null;
        }
        LocalDateTime createdAt = user.getCreatedAt();
        if ( createdAt == null ) {
            return null;
        }
        return createdAt;
    }
}

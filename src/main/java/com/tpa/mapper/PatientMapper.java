package com.tpa.mapper;

import com.tpa.dto.request.PatientRequest;
import com.tpa.dto.response.PatientResponse;
import com.tpa.entity.Patient;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserMapper.class})
public interface PatientMapper {

    @Mapping(source = "patientName", target = "user.username")
    @Mapping(source = "email", target = "user.email")
    @Mapping(source = "mobile", target = "user.phoneNumber")
    @Mapping(source = "dateOfBirth", target = "user.dateOfBirth")
    @Mapping(source = "address", target = "user.address")
    @Mapping(source = "gender", target = "user.gender")
    @Mapping(target = "user.password", ignore = true)
    Patient toPatient(PatientRequest patientRequest);

    @Mapping(source = "user.username", target = "name")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.phoneNumber", target = "phoneNumber")
    @Mapping(source = "user.dateOfBirth", target = "dateOfBirth")
    @Mapping(source = "user.address", target = "address")
    @Mapping(source = "user.gender", target = "gender")
    @Mapping(source = "user.userRole", target = "patientRole")
    @Mapping(source = "user.userStatus", target = "patientStatus")
    @Mapping(source = "user.createdAt", target = "createdAt")
    PatientResponse toPatientResponse(Patient patient);

    List<PatientResponse> toPatientResponses(List<Patient> patients);

    @BeanMapping(ignoreByDefault = true)
    // Delegate to User updates in service, or handle here if properties match
    void updateEntityFromDto(PatientRequest patientRequest, @MappingTarget Patient patient);
}
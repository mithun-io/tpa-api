package com.tpa.mapper;

import com.tpa.dto.request.PatientRequest;
import com.tpa.dto.response.PatientResponse;
import com.tpa.entity.Patient;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-08T19:44:21+0530",
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

        return patient.build();
    }

    @Override
    public PatientResponse toPatientResponse(Patient patient) {
        if ( patient == null ) {
            return null;
        }

        PatientResponse patientResponse = new PatientResponse();

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
}

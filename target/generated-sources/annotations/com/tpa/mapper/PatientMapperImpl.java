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
    date = "2026-05-14T15:11:09+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class PatientMapperImpl implements PatientMapper {

    @Override
    public Patient toPatient(PatientRequest patientRequest) {
        if ( patientRequest == null ) {
            return null;
        }

        Patient patient = new Patient();

        return patient;
    }

    @Override
    public PatientResponse toPatientResponse(Patient patient) {
        if ( patient == null ) {
            return null;
        }

        PatientResponse patientResponse = new PatientResponse();

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

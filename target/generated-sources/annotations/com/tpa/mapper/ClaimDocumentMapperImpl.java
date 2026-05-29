package com.tpa.mapper;

import com.tpa.dto.response.claim.ClaimDocumentResponse;
import com.tpa.entity.ClaimDocument;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-29T12:56:59+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class ClaimDocumentMapperImpl implements ClaimDocumentMapper {

    @Override
    public ClaimDocumentResponse toResponse(ClaimDocument document) {
        if ( document == null ) {
            return null;
        }

        ClaimDocumentResponse.ClaimDocumentResponseBuilder claimDocumentResponse = ClaimDocumentResponse.builder();

        claimDocumentResponse.id( document.getId() );
        claimDocumentResponse.fileName( buildFileUrl( document.getFileName() ) );
        claimDocumentResponse.fileType( buildFileUrl( document.getFileType() ) );
        if ( document.getValidationStatus() != null ) {
            claimDocumentResponse.validationStatus( document.getValidationStatus().name() );
        }
        claimDocumentResponse.validationIssues( buildFileUrl( document.getValidationIssues() ) );
        if ( document.getConfidenceScore() != null ) {
            claimDocumentResponse.confidenceScore( document.getConfidenceScore().doubleValue() );
        }

        claimDocumentResponse.documentType( document.getType().name() );
        claimDocumentResponse.fileUrl( buildFileUrl(document.getFilePath()) );

        return claimDocumentResponse.build();
    }

    @Override
    public List<ClaimDocumentResponse> toResponses(List<ClaimDocument> documents) {
        if ( documents == null ) {
            return null;
        }

        List<ClaimDocumentResponse> list = new ArrayList<ClaimDocumentResponse>( documents.size() );
        for ( ClaimDocument claimDocument : documents ) {
            list.add( toResponse( claimDocument ) );
        }

        return list;
    }
}

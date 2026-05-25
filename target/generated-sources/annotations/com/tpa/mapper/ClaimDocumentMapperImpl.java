package com.tpa.mapper;

import com.tpa.dto.response.claim.ClaimDocumentResponse;
import com.tpa.entity.ClaimDocument;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-25T17:23:49+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class ClaimDocumentMapperImpl implements ClaimDocumentMapper {

    @Override
    public ClaimDocumentResponse toResponse(ClaimDocument document) {
        if ( document == null ) {
            return null;
        }

        ClaimDocumentResponse.ClaimDocumentResponseBuilder claimDocumentResponse = ClaimDocumentResponse.builder();

        if ( document.getConfidenceScore() != null ) {
            claimDocumentResponse.confidenceScore( document.getConfidenceScore().doubleValue() );
        }
        claimDocumentResponse.fileName( buildFileUrl( document.getFileName() ) );
        claimDocumentResponse.fileType( buildFileUrl( document.getFileType() ) );
        claimDocumentResponse.id( document.getId() );
        claimDocumentResponse.validationIssues( buildFileUrl( document.getValidationIssues() ) );
        if ( document.getValidationStatus() != null ) {
            claimDocumentResponse.validationStatus( document.getValidationStatus().name() );
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

package com.tpa.mapper;

import com.tpa.dto.response.claim.ClaimDocumentResponse;
import com.tpa.entity.ClaimDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.io.File;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClaimDocumentMapper {

    @Mapping(target = "documentType", expression = "java(document.getType().name())")
    @Mapping(target = "fileUrl", source = "filePath", qualifiedByName = "buildFileUrl")
    ClaimDocumentResponse toResponse(ClaimDocument document);

    List<ClaimDocumentResponse> toResponses(List<ClaimDocument> documents);

    @Named("buildFileUrl")
    default String buildFileUrl(String path) {
        if (path == null) {
            return null;
        }
        return "/uploads/" + new File(path).getName();
    }
}
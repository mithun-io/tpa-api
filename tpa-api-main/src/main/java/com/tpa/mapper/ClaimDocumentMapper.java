package com.tpa.mapper;

import com.tpa.dto.response.claim.ClaimDocumentResponse;
import com.tpa.entity.ClaimDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.io.File;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClaimDocumentMapper {

    @Mapping(target = "documentType", expression = "java(document.getType().name())")
    @Mapping(target = "fileUrl", expression = "java(buildFileUrl(document.getFilePath()))")
    ClaimDocumentResponse toResponse(ClaimDocument document);

    List<ClaimDocumentResponse> toResponses(List<ClaimDocument> documents);

    default String buildFileUrl(String path) {
        return "/uploads/" + new File(path).getName();
    }
}
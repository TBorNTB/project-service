package com.sejong.projectservice.domains.document.controller;

import com.sejong.projectservice.domains.document.dto.DocumentCreateReq;
import com.sejong.projectservice.domains.document.dto.DocumentInfoRes;
import com.sejong.projectservice.domains.document.dto.DocumentUpdateReq;
import com.sejong.projectservice.domains.document.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/document")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/{projectId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "다큐먼트 생성")
    public ResponseEntity<DocumentInfoRes> createDocumentInProject(
            @Parameter(hidden= true) @RequestHeader("X-User-Id") String username,
            @PathVariable(name = "projectId") Long projectId,
            @RequestBody DocumentCreateReq request
    ) {
        DocumentInfoRes response = documentService.createDocument(projectId, request, username);
        return ResponseEntity
                .status(201)
                .body(response);
    }

    @GetMapping("/{documentId}")
    @Operation(summary = "다큐먼트 상세 조회")
    public ResponseEntity<DocumentInfoRes> getDocument(
            @PathVariable(name = "documentId") Long documentId
    ) {
        DocumentInfoRes documentInfoRes = documentService.getDocument(documentId);
        return ResponseEntity.ok(documentInfoRes);
    }

    @PutMapping("/{documentId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "다큐먼트 수정")
    public ResponseEntity<DocumentInfoRes> updateDocument(
            @Parameter(hidden= true) @RequestHeader("X-User-Id") String username,
            @PathVariable(name = "documentId") Long documentId,
            @RequestBody DocumentUpdateReq request
    ) {
        DocumentInfoRes documentInfoRes = documentService.updateDocument(documentId, request, username);
        return ResponseEntity.ok(documentInfoRes);
    }

    @DeleteMapping("/{documentId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "다큐먼트 삭제")
    public ResponseEntity<Void> deleteDocument(
            @Parameter(hidden= true)  @RequestHeader("X-User-Id") String username,
            @PathVariable(name = "documentId") Long documentId
    ) {

        documentService.deleteDocument(documentId, username);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

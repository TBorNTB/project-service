package com.sejong.projectservice.application.document.controller;

import com.sejong.projectservice.application.document.service.DocumentService;
import com.sejong.projectservice.application.project.dto.request.DocumentCreateReq;
import com.sejong.projectservice.application.project.dto.request.DocumentUpdateReq;
import com.sejong.projectservice.application.project.dto.response.DocumentInfoRes;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/document")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/{projectId}")
    @Operation(summary = "다큐먼트 생성")
    public ResponseEntity<DocumentInfoRes> createDocumentInProject(
            @PathVariable(name = "projectId") Long projectId,
            @RequestBody DocumentCreateReq request
    ) {
        // Todo: member 권한 검증
        DocumentInfoRes response = documentService.createDocument(projectId, request);
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
    @Operation(summary = "다큐먼트 수정")
    public ResponseEntity<DocumentInfoRes> updateDocument(
            @PathVariable(name = "documentId") Long documentId,
            @RequestBody DocumentUpdateReq request
    ) {
        // Todo: member 권한 검증
        DocumentInfoRes documentInfoRes = documentService.updateDocument(documentId, request);
        return ResponseEntity.ok(documentInfoRes);
    }

    @DeleteMapping("/{documentId}")
    @Operation(summary = "다큐먼트 삭제")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable(name = "documentId") Long documentId
    ) {
        // Todo: member 권한 검증
        documentService.deleteDocument(documentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Todo: pageable(cursor/offset), search?
}

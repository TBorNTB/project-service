package com.sejong.projectservice.application.document.controller;

import com.sejong.projectservice.application.document.dto.DocumentCreateReq;
import com.sejong.projectservice.application.document.dto.DocumentInfoRes;
import com.sejong.projectservice.application.document.dto.DocumentUpdateReq;
import com.sejong.projectservice.application.document.service.DocumentService;
import com.sejong.projectservice.core.document.domain.DocumentDocument;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/suggestion")
    @Operation(summary = "elastic 검색 조회 == 쿠팡 검색 추천처럼 ")
    public ResponseEntity<List<String>> getSuggestion(
            @RequestParam String query
    ) {
        List<String> suggestions = documentService.getSuggestions(query);
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/search")
    @Operation(summary = "Document관련 elastic 내용물 전체 조회 => 현재 정렬 방식은 지원 안함")
    public ResponseEntity<List<DocumentDocument>> searchDocuments(
            @RequestParam String query,
            @RequestParam(defaultValue ="5") int size,
            @RequestParam(defaultValue = "0") int page

    ) {

        List<DocumentDocument> response = documentService.searchDocuments(
                query, size,page
        );
        return ResponseEntity.ok(response);
    }
}

package com.sejong.projectservice.application.document.service;

import com.sejong.projectservice.application.document.dto.DocumentCreateReq;
import com.sejong.projectservice.application.document.dto.DocumentInfoRes;
import com.sejong.projectservice.application.document.dto.DocumentUpdateReq;
import com.sejong.projectservice.application.project.assembler.Assembler;
import com.sejong.projectservice.core.document.domain.Document;
import com.sejong.projectservice.core.document.domain.DocumentDoc;
import com.sejong.projectservice.core.document.repository.DocumentElasticRepository;
import com.sejong.projectservice.core.document.repository.DocumentRepository;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentElasticRepository documentElasticRepository;

    @Transactional
    public DocumentInfoRes createDocument(Long projectId, DocumentCreateReq request) {
        Document document = Assembler.toDocument(request, generateYorkieDocumentId(), projectId);
        Document savedDocument = documentRepository.save(document);
        documentElasticRepository.save(savedDocument);
        return DocumentInfoRes.from(savedDocument);
    }

    private String generateYorkieDocumentId() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 7);
    }

    public DocumentInfoRes getDocument(Long documentId) {
        Document document = documentRepository.findById(documentId);
        return DocumentInfoRes.from(document);
    }

    @Transactional
    public DocumentInfoRes updateDocument(Long documentId, DocumentUpdateReq request) {
        Document document = documentRepository.findById(documentId);
        document.update(request.getTitle(), request.getContent(), request.getDescription(), request.getThumbnailUrl());
        Document savedDocument = documentRepository.save(document);
        documentElasticRepository.save(savedDocument);
        return DocumentInfoRes.from(savedDocument);
    }

    @Transactional
    public void deleteDocument(Long documentId) {
        Document document = documentRepository.findById(documentId);
        documentRepository.delete(document);
        documentElasticRepository.deleteById(documentId);
    }

    public List<String> getSuggestions(String query) {
       return documentElasticRepository.getSuggestions(query);
    }

    public List<DocumentDoc> searchDocuments(String query, int size, int page) {
        return documentElasticRepository.searchDocuments(query,size,page);
    }

}

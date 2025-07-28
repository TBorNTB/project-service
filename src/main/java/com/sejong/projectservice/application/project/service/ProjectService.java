package com.sejong.projectservice.application.project.service;

import com.sejong.projectservice.application.project.assembler.Assembler;
import com.sejong.projectservice.application.project.dto.request.DocumentCreateReq;
import com.sejong.projectservice.application.project.dto.request.DocumentUpdateReq;
import com.sejong.projectservice.application.project.dto.request.ProjectFormRequest;
import com.sejong.projectservice.application.project.dto.request.ProjectUpdateRequest;
import com.sejong.projectservice.application.project.dto.response.DocumentInfoRes;
import com.sejong.projectservice.application.project.dto.response.ProjectAddResponse;
import com.sejong.projectservice.application.project.dto.response.ProjectPageResponse;
import com.sejong.projectservice.application.project.dto.response.ProjectSpecifyInfo;
import com.sejong.projectservice.application.project.dto.response.ProjectUpdateResponse;
import com.sejong.projectservice.core.document.domain.Document;
import com.sejong.projectservice.core.document.repository.DocumentRepository;
import com.sejong.projectservice.core.enums.ProjectStatus;
import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.core.project.repository.ProjectRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final DocumentRepository documentRepository;
//    private final UserClient userClient;

    @Transactional
    public ProjectAddResponse createProject(ProjectFormRequest projectFormRequest) {
        Project project = Assembler.toProject(projectFormRequest);
        Project savedProject = projectRepository.save(project);
        return ProjectAddResponse.from(savedProject.getTitle(), "저장 완료");
    }

    public ProjectPageResponse getAllProjects(Pageable pageable) {

        Page<Project> projectPage = projectRepository.findAll(pageable);
        return ProjectPageResponse.from(projectPage);
    }

    @Transactional
    public ProjectUpdateResponse update(Long projectId, ProjectUpdateRequest projectUpdateRequest) {
        Project project = projectRepository.findOne(projectId);
        project.update(projectUpdateRequest.getTitle(), projectUpdateRequest.getDescription(),
                projectUpdateRequest.getProjectStatus(),
                projectUpdateRequest.getThumbnailUrl());
        Project savedProject = projectRepository.save(project);
        return ProjectUpdateResponse.from(savedProject.getTitle(), "수정 완료");
    }

    public ProjectPageResponse search(String keyword, ProjectStatus status, Pageable pageable) {
        Page<Project> projectPage = projectRepository.searchWithFilters(keyword, status, pageable);
        return ProjectPageResponse.from(projectPage);
    }

    public ProjectSpecifyInfo findOne(Long projectId) {
        Project project = projectRepository.findOne(projectId);
        return ProjectSpecifyInfo.from(project);
    }

    @Transactional
    public DocumentInfoRes createDocument(Long projectId, DocumentCreateReq request) {
        Project project = projectRepository.findOne(projectId);
        Document document = Assembler.toDocument(request, generateYorkieDocumentId());
        project.addDocument(document);
        Project savedProject = projectRepository.save(project);

        Document savedDocument = savedProject.getDocuments().stream()
                .filter(d -> d.getYorkieDocumentId().equals(document.getYorkieDocumentId()))
                .findFirst()
                .orElseThrow();
        return DocumentInfoRes.from(savedDocument);
    }

    private String generateYorkieDocumentId() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 7);
    }

    public DocumentInfoRes getDocument(Long projectId, Long documentId) {
        projectRepository.findOne(projectId);
        Document document = documentRepository.findById(documentId);
        return DocumentInfoRes.from(document);
    }

    @Transactional
    public DocumentInfoRes updateDocument(Long projectId, Long documentId, DocumentUpdateReq request) {
        Document document = documentRepository.findById(documentId);
        document.update(request.getTitle(), request.getContent(), request.getDescription(), request.getThumbnailUrl());
        Document savedDocument = documentRepository.save(document);
        return DocumentInfoRes.from(savedDocument);
    }

    @Transactional
    public void deleteDocument(Long projectId, Long documentId) {
        projectRepository.findOne(projectId);
        Document document = documentRepository.findById(documentId);
        documentRepository.delete(document);
    }
}

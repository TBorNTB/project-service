package com.sejong.projectservice.domains.techstack.service;

import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.repository.ProjectRepository;
import com.sejong.projectservice.domains.techstack.domain.TechStackEntity;
import com.sejong.projectservice.domains.techstack.dto.TechStackCreateReq;
import com.sejong.projectservice.domains.techstack.dto.TechStackRes;
import com.sejong.projectservice.domains.techstack.repository.TechStackRepository;
import com.sejong.projectservice.support.common.constants.Type;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import com.sejong.projectservice.support.common.file.FileUploader;
import com.sejong.projectservice.support.outbox.OutBoxFactory;
import com.sejong.projectservice.support.outbox.OutboxService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TechStackService {

    private final TechStackRepository techStackRepository;
    private final ProjectRepository projectRepository;
    private final OutboxService outboxService;
    private final FileUploader fileUploader;

    @Transactional
    public TechStackRes createTechStack(TechStackCreateReq techstackCreateReq, String userRole) {
        validateAdminRole(userRole);
        TechStackEntity techStack = TechStackEntity.of(techstackCreateReq.getName());
        TechStackEntity savedTechStack = techStackRepository.save(techStack);
        return TechStackRes.from(savedTechStack);
    }

    private void validateAdminRole(String userRole) {
        if (!userRole.equals("ADMIN")) {
            throw new BaseException(ExceptionType.REQUIRED_ADMIN);
        }
    }

    @Transactional(readOnly = true)
    public TechStackRes getTechStack(Long techStackId) {
        TechStackEntity techStackEntity = techStackRepository.findById(techStackId)
                .orElseThrow(() -> new BaseException(ExceptionType.TECHSTACK_NOT_FOUND));
        return TechStackRes.from(techStackEntity);
    }

    @Transactional
    public TechStackRes updateTechStack(Long techStackId, TechStackCreateReq request, String userRole) {
        validateAdminRole(userRole);
        TechStackEntity techStackEntity = techStackRepository.findById(techStackId)
                .orElseThrow(() -> new BaseException(ExceptionType.TECHSTACK_NOT_FOUND));
        techStackEntity.update(request.getName());
        return TechStackRes.from(techStackEntity);
    }

    @Transactional
    public void deleteTechStack(Long techStackId, String userRole) {
        validateAdminRole(userRole);
        TechStackEntity techStackEntity = techStackRepository.findById(techStackId)
                .orElseThrow(() -> new BaseException(ExceptionType.TECHSTACK_NOT_FOUND));
        techStackRepository.deleteById(techStackEntity.getId());
    }

    @Transactional
    public List<TechStackRes> updateProject(String username, Long projectId, List<String> techStackNames) {
        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(ExceptionType.PROJECT_NOT_FOUND));
        projectEntity.validateUserPermission(username);

        List<TechStackEntity> techStacks = techStackNames.stream()
                .map(name -> techStackRepository.findByName(name)
                        .orElseThrow(() -> new BaseException(ExceptionType.TECHSTACK_NOT_FOUND)))
                .toList();

        projectEntity.updateTechStack(techStacks);
        OutBoxFactory outbox = OutBoxFactory.of(projectEntity, fileUploader, Type.UPDATED);
        outboxService.enqueue(outbox);
        return techStacks.stream().map(TechStackRes::from).toList();
    }
}

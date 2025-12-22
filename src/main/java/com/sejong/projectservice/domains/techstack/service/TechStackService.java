package com.sejong.projectservice.domains.techstack.service;

import com.sejong.projectservice.domains.techstack.domain.TechStackEntity;
import com.sejong.projectservice.domains.techstack.repository.TechStackJpaRepository;
import com.sejong.projectservice.support.common.error.code.ErrorCode;
import com.sejong.projectservice.support.common.error.exception.ApiException;
import com.sejong.projectservice.domains.techstack.dto.TechStackCreateReq;
import com.sejong.projectservice.domains.techstack.dto.TechStackRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TechStackService {

    private final TechStackJpaRepository techStackJpaRepository;

    @Transactional
    public TechStackRes createTechStack(TechStackCreateReq techstackCreateReq, String userRole) {
        validateAdminRole(userRole);
        TechStackEntity techStack = TechStackEntity.from(techstackCreateReq);
        TechStackEntity savedTechStack = techStackJpaRepository.save(techStack);
        return TechStackRes.from(savedTechStack);
    }

    private void validateAdminRole(String userRole) {
        if (!userRole.equals("ADMIN")) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "어드민 전용 권한입니다.");
        }
    }

    @Transactional(readOnly = true)
    public TechStackRes getTechStack(Long techStackId) {
        TechStackEntity techStackEntity = techStackJpaRepository.findById(techStackId)
                .orElseThrow(() -> new RuntimeException("TechStack not found"));
        return TechStackRes.from(techStackEntity);
    }

    @Transactional
    public TechStackRes updateTechStack(Long techStackId, TechStackCreateReq request, String userRole) {
        validateAdminRole(userRole);
        TechStackEntity techStackEntity = techStackJpaRepository.findById(techStackId)
                .orElseThrow(() -> new RuntimeException("TechStack not found"));
        techStackEntity.update(request.getName());
        return TechStackRes.from(techStackEntity);
    }

    @Transactional
    public void deleteTechStack(Long techStackId, String userRole) {
        validateAdminRole(userRole);
        techStackJpaRepository.deleteById(techStackId);
    }
}

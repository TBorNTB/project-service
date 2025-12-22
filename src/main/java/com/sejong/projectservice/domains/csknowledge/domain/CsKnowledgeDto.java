package com.sejong.projectservice.domains.csknowledge.domain;


import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import com.sejong.projectservice.domains.csknowledge.enums.TechCategory;
import com.sejong.projectservice.domains.user.UserId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CsKnowledgeDto {

    private Long id;
    private String title;
    private String content;
    private UserId writerId;
    private TechCategory category;
    private LocalDateTime createdAt;

    public void validateOwnerPermission(String username) {
        if (!writerId.userId().equals(username)) {
            throw new BaseException(ExceptionType.FORBIDDEN);
        }
    }

    public void validateOwnerPermission(String username, String userRole) {
        if (!writerId.userId().equals(username) && !userRole.equalsIgnoreCase("ADMIN")) {
            throw new BaseException(ExceptionType.FORBIDDEN);
        }
    }
}

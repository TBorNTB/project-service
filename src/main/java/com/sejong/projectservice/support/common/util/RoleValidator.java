package com.sejong.projectservice.support.common.util;

import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;

public final class RoleValidator {

    private static final String GUEST = "GUEST";

    private RoleValidator() {
    }

    /**
     * userRole이 GUEST이면 "외부인은 권한 없습니다" 예외를 던집니다.
     * project, news, document, CsKnowledge 등 외부인(GUEST) 접근이 제한된 API에서 사용합니다.
     */
    public static void validateNotGuest(String userRole) {
        if (userRole != null && GUEST.equalsIgnoreCase(userRole.trim())) {
            throw new BaseException(ExceptionType.GUEST_FORBIDDEN);
        }
    }
}

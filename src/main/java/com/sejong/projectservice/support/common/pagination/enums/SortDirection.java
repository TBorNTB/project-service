package com.sejong.projectservice.support.common.pagination.enums;



import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;

import java.util.Arrays;

public enum SortDirection {
    ASC, DESC;

    public static SortDirection from(String name) {
        return Arrays.stream(SortDirection.values())
                .filter(s -> s.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new BaseException(ExceptionType.SORT_DIRECTION));
    }
}

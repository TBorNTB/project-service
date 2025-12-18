package com.sejong.archiveservice.core.common.pagination;

import lombok.Getter;

@Getter
public class OffsetPageResponse<T> {
    private String message;
    private int page;
    private int totalPage;
    private T data;

    public OffsetPageResponse(String message, int page, int totalPage, T data) {
        this.message = message;
        this.page = page;
        this.totalPage = totalPage;
        this.data = data;
    }

    public static <T> OffsetPageResponse<T> of (String message, int page, int totalPage, T data) {
        return new OffsetPageResponse<>(message, page, totalPage, data);
    }

    public static <T> OffsetPageResponse<T> ok(int page, int totalPage, T data) {
        return of("조회성공", page, totalPage, data);
    }
}

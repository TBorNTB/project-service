package com.sejong.projectservice.application.pagination;


import com.sejong.projectservice.core.common.pagination.Cursor;
import com.sejong.projectservice.core.common.pagination.CursorPageRequest;
import com.sejong.projectservice.core.common.pagination.enums.SortDirection;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CursorPageReqDto {

    private Cursor cursor; // null이면 첫 페이지

    @Min(value = 1, message = "페이지 크기는 최소 1이어야 합니다.")
    @Max(value = 100, message = "페이지 크기는 최대 100이어야 합니다.")
    private int size = 10;

    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "정렬 기준은 알파벳, 숫자, 또는 언더스코어(_)만 포함할 수 있습니다.")
    private String sortBy = "id";

    @Pattern(regexp = "^(ASC|DESC)$", message = "정렬 방향은 'ASC' 또는 'DESC'만 가능합니다.")
    private String sortDirection = "DESC";

    public CursorPageRequest toPageRequest() {
        return CursorPageRequest.of(cursor, size, sortBy, SortDirection.valueOf(sortDirection));
    }
}

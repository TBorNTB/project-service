package com.sejong.projectservice.application.pagination;

import com.sejong.projectservice.core.common.pagination.CustomPageRequest;
import com.sejong.projectservice.core.common.pagination.enums.SortDirection;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OffsetPageReqDto {

    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
    private int page = 0;

    @Min(value = 1, message = "페이지 크기는 최소 1이어야 합니다.")
    @Max(value = 100, message = "페이지 크기는 최대 100이어야 합니다.")
    private int size = 10;

    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "정렬 기준은 알파벳, 숫자, 또는 언더스코어(_)만 포함할 수 있습니다.")
    private String sortBy = "id";

    @Pattern(regexp = "^(ASC|DESC)$", message = "정렬 방향은 'ASC' 또는 'DESC'만 가능합니다.")
    private String sortDirection = "ASC";


    public CustomPageRequest toPageRequest() {
        return CustomPageRequest.of(page, size, sortBy, SortDirection.from(sortDirection));
    }
}

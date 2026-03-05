package com.sejong.projectservice.domains.category.dto.event;

import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import com.sejong.projectservice.support.common.constants.Type;
import com.sejong.projectservice.support.common.file.FileUploader;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryEvent {

    private Long aggregateId;
    private Type type;
    private long occurredAt;
    private CategoryPayload categoryPayload;

    public static CategoryEvent of(CategoryEntity category, FileUploader fu, Type type, long occuredAt) {
        CategoryPayload payload = CategoryPayload.from(category, fu);
        return CategoryEvent.builder()
                .aggregateId(payload.getId())
                .type(type)
                .occurredAt(occuredAt)
                .categoryPayload(payload)
                .build();
    }

    public static CategoryEvent deleteOf(Long categoryId, Type type, long occuredAt) {
        return CategoryEvent.builder()
                .aggregateId(categoryId)
                .type(type)
                .occurredAt(occuredAt)
                .build();
    }
}

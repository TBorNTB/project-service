package com.sejong.projectservice.core.common;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageSearchCommand {
    private int size;
    private int page;
    private String sort;
    private String direction;

    public static PageSearchCommand of(int size, int page, String sort, String direction) {
        return PageSearchCommand.builder()
                .page(page)
                .size(size)
                .sort(sort)
                .direction(direction)
                .build();
    }

}

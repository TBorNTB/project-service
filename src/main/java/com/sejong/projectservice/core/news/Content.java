package com.sejong.archiveservice.core.news;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
@AllArgsConstructor
public class Content {
    private  String title;
    private  String summary;
    private  String content;
    private  NewsCategory category;


    public static Content of(String title, String summary, String content, NewsCategory category) {
        return new Content(title, summary, content, category);
    }
}


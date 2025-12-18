package com.sejong.archiveservice.application.news.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsReqDto {
    String title;
    String summary;
    String content;
    String category;
    String writerUsername;
    List<String> participantIds;
    List<String> tags;

    public void setWriter(String username) {
        writerUsername = username;
    }
}

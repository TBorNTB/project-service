package com.sejong.archiveservice.application.news.assembler;

import com.sejong.archiveservice.application.news.dto.NewsReqDto;
import com.sejong.archiveservice.core.news.Content;
import com.sejong.archiveservice.core.news.News;
import com.sejong.archiveservice.core.news.NewsCategory;
import com.sejong.archiveservice.core.user.UserId;
import com.sejong.archiveservice.core.user.UserIds;
import lombok.experimental.UtilityClass;

@UtilityClass
public class NewsAssembler {
    public static News toNews(NewsReqDto reqDto) {
        Content content = Content.of(reqDto.getTitle(), reqDto.getSummary(), reqDto.getContent(),
                NewsCategory.of(reqDto.getCategory()));
        UserId userId = UserId.of(reqDto.getWriterUsername());
        UserIds userIds = UserIds.of(reqDto.getParticipantIds());

        return News.create(content, userId, userIds, reqDto.getTags());
    }

    public static Content toContent(NewsReqDto reqDto) {
        return Content.of(reqDto.getTitle(), reqDto.getSummary(), reqDto.getContent(), NewsCategory.of(reqDto.getCategory()));
    }
}

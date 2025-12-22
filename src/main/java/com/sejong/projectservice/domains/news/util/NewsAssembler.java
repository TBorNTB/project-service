package com.sejong.projectservice.domains.news.util;


import com.sejong.projectservice.domains.news.dto.NewsReqDto;
import com.sejong.projectservice.domains.news.domain.Content;
import com.sejong.projectservice.domains.news.dto.NewsDto;
import com.sejong.projectservice.support.common.constants.NewsCategory;
import com.sejong.projectservice.domains.user.UserId;
import com.sejong.projectservice.domains.user.UserIds;
import lombok.experimental.UtilityClass;

@UtilityClass
public class NewsAssembler {
    public static NewsDto toNews(NewsReqDto reqDto) {
        Content content = Content.of(reqDto.getTitle(), reqDto.getSummary(), reqDto.getContent(),
                NewsCategory.of(reqDto.getCategory()));
        UserId userId = UserId.of(reqDto.getWriterUsername());
        UserIds userIds = UserIds.of(reqDto.getParticipantIds());

        return NewsDto.create(content, userId, userIds, reqDto.getTags());
    }

    public static Content toContent(NewsReqDto reqDto) {
        return Content.of(reqDto.getTitle(), reqDto.getSummary(), reqDto.getContent(), NewsCategory.of(reqDto.getCategory()));
    }
}

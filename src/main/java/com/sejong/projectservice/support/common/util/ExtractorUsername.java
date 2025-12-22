package com.sejong.projectservice.support.common.util;



import com.sejong.projectservice.domains.csknowledge.dto.CsKnowledgeDto;
import com.sejong.projectservice.domains.news.dto.NewsDto;

import java.util.List;

public class ExtractorUsername {

    public static List<String> FromKnowledges(List<CsKnowledgeDto> knowledges) {
        return knowledges.stream()
                .map(knowledge -> knowledge.getWriterId().userId())
                .toList();
    }

    public static List<String> FromKnowledge(CsKnowledgeDto knowledge) {
        return List.of(knowledge.getWriterId().userId());
    }

    public static List<String> FromNewses(NewsDto newsDto) {
        List<String> usernames = newsDto.getParticipantIds().toList();
        String username = newsDto.getWriterId().userId();

        usernames.add(username);
        return usernames;
    }
}

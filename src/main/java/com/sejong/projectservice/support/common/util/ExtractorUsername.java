package com.sejong.projectservice.support.common.util;



import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledgeEntity;
import com.sejong.projectservice.domains.csknowledge.dto.CsKnowledgeDto;
import com.sejong.projectservice.domains.news.dto.NewsDto;

import java.util.List;

public class ExtractorUsername {

    public static List<String> FromKnowledges(List<CsKnowledgeEntity> csKnowledgeEntities) {
        return csKnowledgeEntities.stream()
                .map(CsKnowledgeEntity::getWriterId)
                .toList();
    }

    public static List<String> FromKnowledge(CsKnowledgeEntity knowledge) {
        return List.of(knowledge.getWriterId());
    }


    public static List<String> FromNewses(NewsDto newsDto) {
        List<String> usernames = newsDto.getParticipantIds().toList();
        String username = newsDto.getWriterId().userId();

        usernames.add(username);
        return usernames;
    }
}

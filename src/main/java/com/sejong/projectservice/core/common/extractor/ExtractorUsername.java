package com.sejong.projectservice.core.common.extractor;



import com.sejong.projectservice.core.csknowledge.CsKnowledge;
import com.sejong.projectservice.core.news.News;

import java.util.List;

public class ExtractorUsername {

    public static List<String> FromKnowledges(List<CsKnowledge> knowledges) {
        return knowledges.stream()
                .map(knowledge -> knowledge.getWriterId().userId())
                .toList();
    }

    public static List<String> FromKnowledge(CsKnowledge knowledge) {
        return List.of(knowledge.getWriterId().userId());
    }

    public static List<String> FromNewses(News news) {
        List<String> usernames = news.getParticipantIds().toList();
        String username = news.getWriterId().userId();

        usernames.add(username);
        return usernames;
    }
}

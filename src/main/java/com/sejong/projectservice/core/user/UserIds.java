package com.sejong.archiveservice.core.user;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserIds {
    private List<UserId> userIds;

    private UserIds(List<UserId> userIds) {
        this.userIds = userIds;
    }

    public static UserIds of(String ids) {
        String[] splits = ids.split(",");
        List<String> convertedIds = Arrays.stream(splits).toList();
        return UserIds.of(convertedIds);
    }

    public static UserIds of(List<String> ids) {
        return new UserIds(ids.stream().map(UserId::of).toList());
    }

    public List<String> toList() {
        return userIds.stream()
                .map(UserId::userId)
                .collect(Collectors.toList());
    }
    
    @Override
    public String toString() {
        return userIds.stream()
                .map(u -> String.valueOf(u.userId()))
                .collect(Collectors.joining(","));
    }
}

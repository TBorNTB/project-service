package com.sejong.projectservice.support.common.internal.response;

/**
 * API 응답용 사용자 프로필 정보.
 * username, nickname, realName, profileImageUrl을 한 객체로 묶어 유지보수성을 높인다.
 */
public record UserProfileDto(
        String username,
        String nickname,
        String realName,
        String profileImageUrl
) {
    public static UserProfileDto from(String username, UserNameInfo info) {
        if (info == null) {
            return null;
        }
        return new UserProfileDto(
                username,
                info.nickname(),
                info.realName(),
                info.profileImageUrl()
        );
    }
}

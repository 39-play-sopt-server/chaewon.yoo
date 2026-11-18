package org.sopt.auth.dto.response;

import org.sopt.auth.domain.SocialProvider;

public record SocialUserInfo(SocialProvider provider, String providerUserId, String name, String email) {}

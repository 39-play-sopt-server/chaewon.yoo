package org.sopt.auth.oauth;

import org.sopt.auth.dto.response.SocialUserInfo;

import java.util.Map;

public interface SocialUserInfoExtractor {

    boolean supports(String registrationId);

    SocialUserInfo extract(Map<String, Object> attributes);
}

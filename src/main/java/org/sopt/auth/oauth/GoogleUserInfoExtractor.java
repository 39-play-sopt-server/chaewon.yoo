package org.sopt.auth.oauth;

import org.sopt.auth.domain.SocialProvider;
import org.sopt.auth.dto.response.SocialUserInfo;
import org.sopt.global.code.GlobalErrorCode;
import org.sopt.global.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GoogleUserInfoExtractor implements SocialUserInfoExtractor {

    @Override
    public boolean supports(String registrationId) {
        return "google".equals(registrationId);
    }

    @Override
    public SocialUserInfo extract(Map<String, Object> attributes) {
        String userId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        if (userId == null || userId.isBlank() || userId.length() > 255
                || email == null || email.isBlank() || email.length() > 255
                || !Boolean.TRUE.equals(attributes.get("email_verified"))) {
            throw new BusinessException(GlobalErrorCode.SOCIAL_LOGIN_FAILED);
        }
        if (name == null || name.isBlank()) {
            name = email;
        }
        name = name.strip();
        if (name.length() > 100) {
            name = name.substring(0, 100);
        }
        return new SocialUserInfo(SocialProvider.GOOGLE, userId, name, email);
    }
}

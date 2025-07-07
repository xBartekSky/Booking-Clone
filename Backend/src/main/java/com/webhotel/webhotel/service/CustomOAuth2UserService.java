package com.webhotel.webhotel.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

import com.webhotel.webhotel.config.AuthProvider;
import com.webhotel.webhotel.dto.UserDto;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;
    private final AuthProvider authProvider;

    public CustomOAuth2UserService(UserService userService, AuthProvider authProvider) {
        this.userService = userService;
        this.authProvider = authProvider;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String username = oAuth2User.getAttribute("login");
        String email = oAuth2User.getAttribute("email");

        UserDto userDto;
        try {
            userDto = userService.findByUsername(username);
        } catch (Exception e) {
            userDto = userService.createUserFromOAuth(username, email);
        }

        String token = authProvider.createToken(userDto.getUsername());

        userDto.setToken(token);

        return new DefaultOAuth2User(oAuth2User.getAuthorities(), oAuth2User.getAttributes(), "login");
    }
}
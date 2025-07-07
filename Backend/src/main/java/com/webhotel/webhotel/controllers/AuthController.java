package com.webhotel.webhotel.controllers;

import java.net.URI;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.webhotel.webhotel.config.AuthProvider;
import com.webhotel.webhotel.dto.LoginDto;
import com.webhotel.webhotel.dto.RegisterDto;
import com.webhotel.webhotel.dto.UserDto;
import com.webhotel.webhotel.dto.UserInfoDto;
import com.webhotel.webhotel.entity.User;
import com.webhotel.webhotel.mapper.UserMapper;
import com.webhotel.webhotel.repository.UserRepository;
import com.webhotel.webhotel.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
public class AuthController {
    private final UserService userService;
    private final AuthProvider authProvider;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public AuthController(UserService userService, AuthProvider authProvider, UserRepository userRepository,
            UserMapper userMapper) {
        this.userService = userService;
        this.authProvider = authProvider;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody @Valid RegisterDto user) {
        UserDto createdUser = userService.register(user);
        return ResponseEntity.created(URI.create("/users/" + createdUser.getId())).body(createdUser);
    }

    @PostMapping("/api/login")
    public ResponseEntity<UserDto> login(@RequestBody @Valid LoginDto loginDto, HttpServletResponse response) {
        UserDto userDto = userService.login(loginDto);
        String accessToken = authProvider.createToken(userDto.getUsername());
        String refreshToken = authProvider.createRefreshToken(userDto.getUsername());

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                 .httpOnly(true)
                //  .secure(false)
                //  .path("/api/refresh")
                .maxAge(7 * 24 * 60 * 60 *1000)
                // .sameSite("Strict")
                .build();

        response.setHeader("Set-Cookie", refreshCookie.toString());

        userDto.setToken(accessToken);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/api/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is missing");
        }

        try {
            Authentication auth = authProvider.validateRefreshToken(refreshToken);
            String username = ((UserDto) auth.getPrincipal()).getUsername();
            String newAccessToken = authProvider.createToken(username);

            return ResponseEntity.ok(Map.of("token", newAccessToken));
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
    }

    @DeleteMapping("/api/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                // .secure(true)
                 .path("/api/refresh")
                .maxAge(0)
                // .sameSite("Strict")
                .build();

        response.setHeader("Set-Cookie", deleteCookie.toString());

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @GetMapping("/auth/github")
    public ResponseEntity<Void> handleGithubLogin(OAuth2AuthenticationToken authentication,
            HttpServletResponse response) {
        OAuth2User user = authentication.getPrincipal();
        String username = user.getAttribute("login");
        String email = user.getAttribute("email");

        UserDto userDto = userService.findByUsername(username);
        if (userDto == null) {
            userDto = userService.createUserFromOAuth(username, email);
        }

        String token = authProvider.createToken(userDto.getUsername());
        String refreshToken = authProvider.createRefreshToken(userDto.getUsername());

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/api/refresh")
                .maxAge(15*60*1000)
                .sameSite("Strict")
                .build();
        response.setHeader("Set-Cookie", cookie.toString());

        String frontendUrl = "http://localhost:5173/login?token=" + token;
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", frontendUrl)
                .build();
    }

    @GetMapping("/myaccount")
    public ResponseEntity<UserDto> getCurrentUser() {
        String username = userService.getCurrentUsernameFromToken();
        UserDto userDto = userService.findByUsername(username);

        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/userinfo")
    public ResponseEntity<UserInfoDto> getUserInfo(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        Authentication authentication = authProvider.validateToken(token);
        String username = ((UserDto) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserInfoDto userInfoDto = userMapper.userToUserInfoDto(user);

        return ResponseEntity.ok(userInfoDto);
    }

    @PutMapping("/userinfo")
    public ResponseEntity<UserInfoDto> updateUserInfo(@RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody UserInfoDto userInfoDto) {
        String token = authorizationHeader.replace("Bearer ", "");
        Authentication authentication = authProvider.validateToken(token);
        String username = ((UserDto) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getUsername().equals(userInfoDto.getUsername())) {
            boolean usernameExists = userRepository.existsByUsername(userInfoDto.getUsername());
            if (usernameExists) {
                throw new RuntimeException("Username already taken.");
            }
        }
        Long userid = user.getId();
        String userpass = user.getPassword();
        
        user = userMapper.userInfoDtoToUser(userInfoDto);
        user.setId(userid);
        user.setPassword(userpass);
        user.setAddress(user.getAddress());
        user.setName(user.getName());
        user.setSurname(user.getSurname());
        user.setPhoneNumber(user.getPhoneNumber());

        userRepository.save(user);

        UserInfoDto updatedUserInfoDto = userMapper.userToUserInfoDto(user);
        return ResponseEntity.ok(updatedUserInfoDto);
    }

}

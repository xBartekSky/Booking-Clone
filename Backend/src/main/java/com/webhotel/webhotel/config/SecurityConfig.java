package com.webhotel.webhotel.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
// public class SecurityConfig {

// @Bean
// public SecurityFilterChain securityFilterChain(HttpSecurity http) throws
// Exception {
// http
// .csrf().disable()
// .cors().and()
// .authorizeHttpRequests((requests) -> requests
// .requestMatchers(HttpMethod.POST, "/login", "/register").permitAll()
// .requestMatchers("/auth/github").authenticated()
// .anyRequest().permitAll()
// )
// .oauth2Login(oauth -> oauth
// .defaultSuccessUrl("/auth/github", true)
// )
// .formLogin().disable()
// .exceptionHandling()
// .authenticationEntryPoint((request, response, authException) -> {
// response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
// });

// return http.build();
// }
// }

public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(HttpMethod.POST, "/api/login", "/register").permitAll()
                        .requestMatchers("/auth/github").authenticated()
                        .anyRequest().permitAll())
        
                .oauth2Login().permitAll();
        return http.build();
    }
}
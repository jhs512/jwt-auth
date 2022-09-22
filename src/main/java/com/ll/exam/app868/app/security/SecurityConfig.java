package com.ll.exam.app868.app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.exam.app868.app.member.repository.MemberRepository;
import com.ll.exam.app868.app.security.filter.JwtAuthenticationFilter;
import com.ll.exam.app868.app.security.filter.JwtAuthorizationFilter;
import com.ll.exam.app868.app.security.handler.LoginFailHandler;
import com.ll.exam.app868.app.security.handler.LoginSuccessHandler;
import com.ll.exam.app868.app.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailHandler loginFailHandler;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationConfiguration authenticationConfiguration) throws Exception {
        http
                .cors().disable()
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(STATELESS)
                )
                .addFilterBefore(new JwtAuthenticationFilter(
                        authenticationConfiguration.getAuthenticationManager(), loginSuccessHandler, loginFailHandler),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthorizationFilter(jwtProvider, objectMapper, memberRepository), JwtAuthenticationFilter.class);
        return http.build();
    }
}

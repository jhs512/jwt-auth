package com.ll.exam.app868.app.security.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.exam.app868.app.member.entity.Member;
import com.ll.exam.app868.app.member.repository.MemberRepository;
import com.ll.exam.app868.app.security.context.MemberContext;
import com.ll.exam.app868.app.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String BearerToken = request.getHeader("Authorization");


        if (BearerToken != null) {
            String token = BearerToken.substring(7);
            if (jwtProvider.verifyToken(token)) {
                Member member = memberRepository.findByUsername(jwtProvider.getUsername(token))
                        .orElseThrow(() -> new UsernameNotFoundException("username not found exception"));

                if (token.equals(member.getAccessToken())) {
                    forceAuthentication(token);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    public void forceAuthentication(String token) throws IOException {
        String authoritiesString = jwtProvider.getAuthorities(token);

        List<SimpleGrantedAuthority> authorities =
                objectMapper.readValue(authoritiesString.getBytes(), new TypeReference<>() {
                });


        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        new MemberContext(jwtProvider.getId(token), jwtProvider.getUsername(token), "", authorities),
                        null,
                        authorities
                );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}

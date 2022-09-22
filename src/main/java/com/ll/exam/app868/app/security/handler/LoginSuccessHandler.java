package com.ll.exam.app868.app.security.handler;

import com.ll.exam.app868.app.member.entity.Member;
import com.ll.exam.app868.app.member.repository.MemberRepository;
import com.ll.exam.app868.app.security.context.MemberContext;
import com.ll.exam.app868.app.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.ManyToAny;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Transactional
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        MemberContext memberContext = (MemberContext) authentication.getPrincipal();

        String accessToken = jwtProvider.getAccessToken(memberContext);
        Member member = memberRepository.findByUsername(memberContext.getUsername()).get();
        member.setAccessToken(accessToken);

        response.addHeader("Authentication", accessToken);
    }
}

package com.ll.exam.app868.app.security.service;

import com.ll.exam.app868.app.member.entity.Member;
import com.ll.exam.app868.app.member.repository.MemberRepository;
import com.ll.exam.app868.app.security.context.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class MemberSecurityService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("username not found exception"));

        return new MemberContext(
                member.getId(),
                member.getUsername(),
                member.getPassword(),
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}

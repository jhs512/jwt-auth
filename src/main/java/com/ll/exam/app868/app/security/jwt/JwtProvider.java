package com.ll.exam.app868.app.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.exam.app868.app.security.context.MemberContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final ObjectMapper objectMapper;
    private final String AUTHORITIES = "authorities";
    private final String USERNAME = "username";
    private final String ID = "id";
    private final String BEARER_TYPE = "bearer";
    private final Long ACCESS_TOKEN_EXPIRE_TIME = (1000L * 60L * 60L * 24L * 30L);
    @Value("${custom.jwt.secretKey}")
    private String secretKey;
    private SecretKey key;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String getAccessToken(MemberContext memberContext) throws JsonProcessingException {
        long now = new Date().getTime();
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

        Set<String> authorities = memberContext.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority()).collect(Collectors.toSet());
        String authoritiesString = objectMapper.writeValueAsString(authorities);

        return Jwts.builder()
                .claim(ID, memberContext.getId())
                .claim(USERNAME, memberContext.getUsername())
                .claim(AUTHORITIES, authoritiesString)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean verifyToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            log.info("claims: " + claims);

            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token");
            log.error("Invalid JWT token trace: " + e.getMessage());
        }

        return false;
    }

    public String getAuthorities(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get(AUTHORITIES, String.class);
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get(USERNAME, String.class);
    }

    public Long getId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get(ID, Long.class);
    }
}

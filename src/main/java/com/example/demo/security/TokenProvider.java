package com.example.demo.security;

import com.example.demo.model.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
public class TokenProvider {

    private static final String SECRET_KEY = "FlRpX30pMqDbiAkmlfArbrmVkDD4RqISskGZmBFax5oGVxzXXWUzTR5JyskiHMIV9M1Oicegkpi46AdvrcX1E6CmTUBc6IFbTPiD";
    
    public String create(UserEntity userEntity) {
        // 생성 시, 컴퓨터 서버 시간으로부터 '만료기한'=1일 로 설정.
        Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));

        /*

        { // header
            "alg":"HS512"
        }.{ // payload
            "sub":"40288093784915d201784916a40c0001",
            "iss": "demo app",
            "iat":1595733657,
            "exp":1596597657
        }.
        // SECRET_KEY를 이용해 서명한 부분
        Nn4d1MOVLZg79sfFACTIpCPKqWmpZMZQsbNrXdJJNWkRv50_l7bPLQPwhMobT4vBOG6Q3JYjhDrKFlBSaUxZOg

        */

        // JWT Token 생성
        return Jwts.builder()
                // signWith : (header에 들어갈 내용, 시크릿키)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                // sub, iss, iat, exp : payload에 들어갈 내용
                .setSubject(userEntity.getId())
                .setIssuer("demo app")
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .compact();

    }

    public String validateAndGetUserId(String token) {
        // interface JwtParser의 parseClaimsJws 메서드가
        // token을 Base64로 디코딩&파싱한다.
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        // 위조되지 않았으면 Claims 인터페이스의 전역프로퍼티(페이로드 정보를 return)
        // 위조면 예외를 날림

        // getBody 메서드 디버깅 보려고 테스트
        String claims2 = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getSignature();

        JwsHeader claims3 = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getHeader();

        return claims.getSubject();
    }
}

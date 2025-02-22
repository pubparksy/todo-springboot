package com.example.demo.security;

import ch.qos.logback.core.util.StringUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Filter is running...");
        try {
            // Request에서 token 추출
            String token = parseBearerToken(request);
            
            // 토큰 검증
            if (token != null && !token.equalsIgnoreCase("null")) {
                // 토큰 검증 하고 userId 추출
                String userId = tokenProvider.validateAndGetUserId(token);
                log.info("Authenticated userID : " + userId);

                // 인증 완료 (Spring은 SecurityContextHolder에 등록하면 '인증 유저'로 인지한다.
                // 유저 인증 정보를 UsernamePasswordAuthenticationToken 인증 객체에 저장.
                AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userId, null, AuthorityUtils.NO_AUTHORITIES);
                // 인증된 유저 정보. 문자열 말고 아무거나 가능. 보통 UserDetails라는 오브젝트를 넣지만, 지금은 미생성.
//                UsernamePasswordAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority > authorities) {
//                    super(authorities);
//                    this.principal = principal;
//                    this.credentials = credentials;
//                    super.setAuthenticated(true);
//                }
                
                // AbstractAuthenticationToken에 (지금 request 웹인증상세소스 객체)를 details로 저장.
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 그러면 authentication는 UsernamePasswordAuthenticationToken 객체지만 Object details 객체도 있는 상태.

                // Spring에 빈 SecurityContextHolder 객체를 만들고,
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authentication); // 위의 인증된 유저 상세가 담긴 객체를 등록한다.
                SecurityContextHolder.setContext(securityContext);
                // ContextHolder는 기본적으로 ThreadLocal에 저장되므로
                // 1.각 스레드마다 하나의 컨텍스트를 관리 2. 같은 스레드 내라면 어디에서든 접근 가능

            } // END extract token from request
        } catch (Exception e) {
            logger.error("Could not set user authentication in security context", e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseBearerToken(HttpServletRequest request) {
        // HTTP Request에서 Header를 parsing해서 return token
        String bearerToken = request.getHeader("Authorization"); // Authorization: Bearer ~ 에서 앞의 key
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // 앞의 7글자, 즉 'Bearer '는 삭제하고 return
        }
        return null;
    }
}

package LikelionSummerStudy.blogSummer.config.jwt;

import LikelionSummerStudy.blogSummer.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TokenProvider {

    private final JwtProperties jwtProperties;

    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        //현재 시간 + 만료 시간 더한 후 makeToken() 호출
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    //JWT 생성하는 메서드, JWT를 빌드해 compact() 메서드를 호출하여 토큰 문자열 반환
    private String makeToken(Date expiry, User user) {
        Date now = new Date(); //현재 시간

        return Jwts.builder() //빌더 시작
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) //JWT type 설정
                .setIssuer(jwtProperties.getIssuer()) //발급자 설정
                .setIssuedAt(now) //발급 시간 설정
                .setExpiration(expiry) //만료 시간 설정
                .setSubject(user.getEmail()) //JWT의 주체(subject)로 사용자 이메일 설정
                .claim("id", user.getId()) //추가 클레임 설정 (사용자 ID 이용)
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey()) //해시 알고리즘 및 비밀키 설정
                .compact(); //// JWT를 최종적으로 생성해 문자열 형태로 반환
    }

    //JWT 토큰의 유효성 검증 메서드
    public boolean validToken(String token) {
        try {
            Jwts.parser() //JWT 분해 시작
                    .setSigningKey(jwtProperties.getSecretKey()) //비밀 키 설정, 이 비밀키로 JWT의 서명이 유효한지 검증
                    .parseClaimsJws(token); //JWT 분해 및 서명 검증, 성공하면 true

            return true; //유효한 JWT라면 true를 반환
        } catch (Exception e) { //예외 처리
            return false;
        }
    }

    //JWT 사용하여 인증 정보를 담은 객체 Authentication을 반환하는 메서드
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token); //JWT에서 클레임 추출
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")); //권한 설정

        //Spring Security에서 인증된 사용자를 나타내는 Authentication 객체 생성
        return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails.User(claims.getSubject
                (), "", authorities), token, authorities);
    }

    //JWT 토큰 기반 사용자 ID를 추출하여 반환
    public Long getUserId(String token) {
        Claims claims = getClaims(token); // 1. 토큰에서 클레임을 추출
        return claims.get("id", Long.class); // 2. "id" 클레임을 Long 타입으로 추출하여 반환
    }

    // JWT 토큰을 파싱하고, 해당 토큰에서 클레임을 추출
    private Claims getClaims(String token) {
        return Jwts.parser()  // 1. JWT 파서 시작
                .setSigningKey(jwtProperties.getSecretKey())  // 2. 서명 검증을 위한 비밀키 설정
                .parseClaimsJws(token)  // 3. JWT 파싱 및 서명 검증
                .getBody();  // 4. JWT 클레임 반환
    }
}
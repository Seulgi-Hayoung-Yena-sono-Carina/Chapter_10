package LikelionSummerStudy.blogSummer.config;

import LikelionSummerStudy.blogSummer.config.jwt.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider; //JWT 토큰의 유효성 검증, Autehntication 개게 생성 담당 클래스

    private final static String HEADER_AUTHORIZATION="Authorization";
    private final static String TOKEN_PREFIX="Bearer ";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        //요청 헤더의 Authorization 키의 값 조회
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        /**
         * String authorizationHeader = request.getHeader("Authorization");
         * "Authorization"에 마우스 커서 올리고 Ctrl + Alt + C 누르면 위 private final static field 자동 생성
         * */

        //가져온 값에서 접두사 제거
        String token=getAccessToken(authorizationHeader);
        //가져온 토콘이 유효한지 확인, 유효한 때는 인증 정보를 설정
        if(tokenProvider.validToken(token)){
            //유효하면 시큐리티 컨텍스트에 인증 정보를 설정
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        //현재 필터에서 작업을 마친 후 다음 필터나 최종 처리 로직(예: 컨트롤러)로 요청을 계속 전달
        filterChain.doFilter(request,response);
    }

    //Authorization 헤더에서 Bearer 접두어 제거하고 실제 JWT 토큰 반환
    private String getAccessToken(String authorizationHeader){
        if(authorizationHeader!=null &&authorizationHeader.startsWith(TOKEN_PREFIX)){
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}

package LikelionSummerStudy.blogSummer.config.oauth;

import LikelionSummerStudy.blogSummer.config.jwt.TokenProvider;
import LikelionSummerStudy.blogSummer.domain.RefreshToken;
import LikelionSummerStudy.blogSummer.domain.User;
import LikelionSummerStudy.blogSummer.repository.RefreshTokenRepository;
import LikelionSummerStudy.blogSummer.service.UserService;
import LikelionSummerStudy.blogSummer.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
@Component // 스프링 설정 클래스
//SimpleUrlAuthenticationSuccessHandler: 로그인 성공 시 기본 리다이렉트 기능을 제공하는 스프링 시큐리티 클래스
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token"; //쿠키에 저장할 refresh token 이름
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14); //refresh token의 유효 기간
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1); //access token의 유효 기간
    public static final String REDIRECT_PATH = "/articles"; //로그인 성공 후 redirect할 경로

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;

    //OAuth2 로그인 성공 시 실행되는 핵심 메서드
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        //OAuth2User로부터 로그인한 사용자 정보(email 등) 추출
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        //DB에서 User 객체 조회
        User user = userService.findByEmail((String) oAuth2User.getAttributes().get("email"));
        //Refresh Token 발급 후 DB에 저장 후 쿠키로 응답
        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION); //Refresh Token 생성
        saveRefreshToken(user.getId(), refreshToken); //DB에 있으면 갱신, 없으면 생성
        addRefreshTokenToCookie(request, response, refreshToken); //기존 refresh_token 삭제 후 새 토큰을 쿠키로 추가
        //Acess Token 발급 후 최종 redirect 경로에 포함
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION); //Access Token 생성
        String targetUrl = getTargetUrl(accessToken); // /articles?token=... 형식의 URL로 redirect 경로를 구성

        clearAuthenticationAttributes(request, response); //authorizationRequestRepository에서 OAuth2 로그인 요청 관련 쿠키 삭제

        getRedirectStrategy().sendRedirect(request, response, targetUrl); //최종적으로 클라이언트를 /articles?token=...으로 이동시킴
    }
    // 기존 토큰 있으면 갱신, 없으면 새로 생성 후 저장
    private void saveRefreshToken(Long userId, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }
    //기존 refresh_token 쿠키 삭제 후, 새 쿠키를 응답에 추가
    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();

        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    //OAuth2 로그인 과정에서 남는 인증 정보 쿠키를 정리
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        //세션에 남아 있을 수 있는 OAuth2 관련 임시 속성들을 정리
        super.clearAuthenticationAttributes(request);
        //쿠키에 저장된 인증 요청 정보 제거
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    ///articles?token=xxx 형식으로 redirect URL 생성
    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("token", token)
                .build()
                .toUriString();
    }
}

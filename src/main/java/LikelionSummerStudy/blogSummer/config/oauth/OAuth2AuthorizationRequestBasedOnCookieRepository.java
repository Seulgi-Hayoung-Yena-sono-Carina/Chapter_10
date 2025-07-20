package LikelionSummerStudy.blogSummer.config.oauth;

import LikelionSummerStudy.blogSummer.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.WebUtils;

//AuthorizationRequestRepository<OAuth2AuthorizationRequest>: OAuth2 인증 요청 정보를 저장/조회/삭제하는 기능 제공
public class OAuth2AuthorizationRequestBasedOnCookieRepository implements //Ctrl+O로 override할 메서드 선택
        AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    public static final String OAUTH_2_AUTHORIZATION_REQUEST_COOKIE_NAME= "oauth2_auth_request";
    public static final int COOKIE_EXPIRE_SECONDS = 18000;

    //쿠키에서 "oauth2_auth_request"라는 이름의 쿠키를 찾아서 그 값을 OAuth2AuthorizationRequest 객체로 역직렬화해서 반환
    //즉, 로그인 요청 상태를 복원
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        //WebUtils.getCookie(request,OAUTH_2_AUTH_REQUEST); 문자열에 커서 올리고 Ctrl+Alt+C
        Cookie cookie = WebUtils.getCookie(request, OAUTH_2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        return CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class);
    }

    //OAuth2 로그인 시작 시 요청 정보를 쿠키에 저장하거나 삭제
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if(authorizationRequest==null){ //null이면 관련 쿠키 삭제
            removeAuthorizationRequestCookies(request,response);
            return;
        }
        //존재하면 직렬화해서 쿠키에 저장
        CookieUtil.addCookie(response,OAUTH_2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                CookieUtil.serialize(authorizationRequest),COOKIE_EXPIRE_SECONDS);
    }

    //Spring Security가 인증 완료 시, 저장된 요청 정보를 삭제하려고 이 메서드를 호출
    //실제 쿠키 삭제는 saveAuthorizationRequest() 내부에서 null일 때만 처리
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return this.loadAuthorizationRequest(request);
    }

    //쿠키 이름 기준으로 삭제 요청을 보냄(maxAge를 0으로 만듦)
    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request,response,OAUTH_2_AUTHORIZATION_REQUEST_COOKIE_NAME);
    }

}

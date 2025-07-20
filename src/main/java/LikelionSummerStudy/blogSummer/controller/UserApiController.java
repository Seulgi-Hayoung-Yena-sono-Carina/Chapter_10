package LikelionSummerStudy.blogSummer.controller;

import LikelionSummerStudy.blogSummer.config.jwt.TokenProvider;
import LikelionSummerStudy.blogSummer.dto.request.AddUserRequest;
import LikelionSummerStudy.blogSummer.repository.RefreshTokenRepository;
import LikelionSummerStudy.blogSummer.service.UserService;
import LikelionSummerStudy.blogSummer.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserApiController {
    /*private final UserService userService;
    @PostMapping("/user")
    public String signup(AddUserRequest request){
        userService.save(request); //UserService 계층의 메서드 호출
        return "redirect:/login"; //로그인 페이지로 redirect
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        //new SecurityContextLogOutHandler(): 사용자의 인증 정보를 초기화하고, 세션을 무효화하여 로그아웃을 처리
        new SecurityContextLogoutHandler()
                .logout(request,response, SecurityContextHolder.getContext().getAuthentication());
        //SecurityContextHolder.getContext().getAuthentication()
        return "redirect:/login";
    }*/
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token"; //쿠키에서 사용할 토큰 이름 상수로 정의

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @DeleteMapping("/refresh-token")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에서 refresh_token 가져오기
        CookieUtil.getCookie(request, REFRESH_TOKEN_COOKIE_NAME)
                .map(Cookie::getValue) //요청에서 "refresh_token" 쿠키를 꺼내고
                .ifPresent(refreshToken -> { //값이 있으면
                    // 유효한 토큰인지 확인 후, 서버 저장소에서 삭제
                    if (tokenProvider.validToken(refreshToken)) { //유효한 토큰이면
                        Long userId = tokenProvider.getUserId(refreshToken); //토큰에 담긴 userId를 꺼내서
                        refreshTokenRepository.findByUserId(userId) //DB에 저장된 refreshToken이 있으면 삭제
                                .ifPresent(refreshTokenRepository::delete);
                    }
                });

        // 쿠키에서도 refeshTOKEN 삭제
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);

        return ResponseEntity.ok().build(); // 상태 코드 200
    }
}

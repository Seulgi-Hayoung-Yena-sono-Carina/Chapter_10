package LikelionSummerStudy.blogSummer.config;

import LikelionSummerStudy.blogSummer.config.jwt.TokenProvider;
import LikelionSummerStudy.blogSummer.config.oauth.OAuth2SuccessHandler;
import LikelionSummerStudy.blogSummer.config.oauth.OAuth2UserCustomService;
import LikelionSummerStudy.blogSummer.repository.RefreshTokenRepository;
import LikelionSummerStudy.blogSummer.service.UserService;
import LikelionSummerStudy.blogSummer.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Configuration// 스프링 설정 클래스
public class WebOAuthSecurityConfig {
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Bean
    public WebSecurityCustomizer configure(){
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console()) // H2 콘솔
                .requestMatchers("/img/**", "/css/**", "/js/**"); // 정적 자원
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
                //토큰 방식으로 인증을 하기 때문에 기존에 사용하던 폼 로그인, 세션 비활성화
                .csrf(AbstractHttpConfigurer::disable) //REST API에서는 세션 기반 인증 안 쓰고 토큰 기반 사용해서 꺼도 됨
                .httpBasic(AbstractHttpConfigurer::disable) //브라우저 팝업의 기본 인증(Username+Password 팝업 로그인) 비활성화
                .formLogin(AbstractHttpConfigurer::disable) //폼 로그인 사용 X
                .logout(AbstractHttpConfigurer::disable) //기본 로그아웃 URL 비활성화
                //세션 관리 전략: 서버가 세션을 아예 생성하지 않음(JWT 기반 인증에서는 서버에 세션 저장 X->필수 설정)
                .sessionManagement(management->management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //헤더를 확인할 커스텀 필터 추가
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                /**
                 * 커스텀 JWT 필터를 Spring Security 필터 체인에 추가
                 * tokenAuthenticationFilter(): 내가 직접 만든 필터, JWT를 검사해 유저를 인증
                 * UsernamePasswordAuthenticationFilter보다 앞에 넣어, JWT 토큰 인증을 가장 먼저 수행
                 * ->form 로그인 없이도 인증이 되도록 한다
                 */

                //토큰 재발급 URL은 인증 없이 접근 가능하도록 설정
                //나머지 API URL은 인증 필요
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/token", "/api/refresh-token").permitAll()      // 누구나 접근 가능
                        .requestMatchers("/api/**").authenticated()       // /api/** → 인증 필요(로그인/토큰 인증이 있어야 접근 가능)
                        .anyRequest().permitAll()    // 그 외는 전부 허용                      // 나머지는 허용
                )
                //Spring Security가 제공하는 OAuth2 로그인 기능을 사용할 때 필요한 설정 블록
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")   //사용자가 로그인이 필요한 페이지에 접근하면 이 경로로 redirect
                        .authorizationEndpoint(authorizationEndpoint-> //OAuth2 로그인 요청 정보를 저장할 저장소(세션 대신 쿠키에 저장하도록 하는 커스텀 저장소)를 지정
                                authorizationEndpoint.authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                        .userInfoEndpoint(userInfoEndpoint ->
                                userInfoEndpoint.userService(oAuth2UserCustomService)
                        ) //OAuth2 인증이 끝난 후, 유저 정보를 가져와서 어떻게 처리할지 정하는 부분
                        //인증 성공 시 실행할 핸들러
                        .successHandler(oAuth2SuccessHandler()) //OAuth2 로그인 성공 후 실행할 커스텀 로직을 정의
                )
                .exceptionHandling(exceptionHandling->exceptionHandling
                        //인증 실패(로그인 안 됨) 상황일 때 어떻게 응답할지 설정하는 메서드
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), //401 return
                                request -> request.getRequestURI().startsWith("/api/") // /api/로 시작하는 모든 API 요청에만 이 룰을 적용
                        )
                )
                .build();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler(){
        return new OAuth2SuccessHandler(tokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService);
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter(){
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository(){
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}

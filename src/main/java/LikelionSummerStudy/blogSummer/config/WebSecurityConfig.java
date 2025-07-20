/*package LikelionSummerStudy.blogSummer.config;

import LikelionSummerStudy.blogSummer.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration // 스프링 설정 클래스
@EnableWebSecurity // Spring Security 웹 보안 기능 활성화
@RequiredArgsConstructor
//Spring Security 설정을 통해 웹 애플리케이션의 보안을 구성하며, 로그인, 로그아웃, 인증, CSRF 방어 등 다양한 보안 기능을 설정하는 클래스
public class WebSecurityConfig {

    private final UserDetailService userService;

    // 1. 스프링 시큐리티 기능 비활성화 (H2 콘솔, 정적 리소스) (기본적으로 모든 요청을 인증 없이 막는 구조라서)
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers("/static/**");
    }

    //2. 특정 HTTP 요청에 대한 웹 기반의 보안 구성
    //SecurityFilterChain: HTTP 요청에 대한 보안 정책 설정하는 필터 체인 정의
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
                .authorizeHttpRequests(auth -> auth //HTTP 요청에 대한 권한을 설정
                        //로그인, 회원가입, 사용자 페이지 경로에 대한 요청을 지정, 인증 없이 허용
                        .requestMatchers("/login", "/signup", "/user").permitAll()  // 수정된 부분
                        .anyRequest().authenticated())  // 나머지 모든 요청은 인증 필요
                .formLogin(formLogin->formLogin //폼 로그인 설정 정의
                        .loginPage("/login") //로그인 페이지 URL 설정
                        .defaultSuccessUrl("/articles") //로그인 성공 후 /articles로 redirect
                )
                .logout(logout->logout //로그아웃 관련 설정 정의
                        .logoutSuccessUrl("/login") //로그아웃 후 redirect할 URL 설정
                        .invalidateHttpSession(true) //로그아웃 시 현재 HTTP 세션 무효화
                )
                .csrf(AbstractHttpConfigurer::disable) //CSRF 공격에 대한 방어 비활성화
                .build(); //SecurityFilterChain 객체 생성 후 반환
    }

    //3. 인증 관리자 관련 설정
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder,
                                                       UserDetailService userDetailService) throws Exception{

        *//**
         *DaoAuthenticationProvider: DB나 다른 저장소에서 사용자 정보를
         * 가져와 인증을 처리하는 Spring Security의 기본 제공 인증 제공자
         * 이 객체: UserDetailService를 통해 사용자 정보를 가져옴
         * PassWordEncoder를 사용해 비밀번호 비교
         **//*
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        // 사용자 이름과 비밀번호를 기반으로 인증을 처리하는 객체 주입받기
        authProvider.setUserDetailsService(userService);

        //비밀번호를 암호화 및 비교할 방법을 설정
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);

        //providerManager: AuthenticationManger의 기본 구현체
        //여러 인증 제공자를 사용하여 인증 수행 가능 ( 여기서는 DaoAuthenticationProvider 하나만 사용)
        return new ProviderManager(authProvider);
    }

    //4. 패스워드 인코더를 빈으로 등록
    //BCryptPasswordEncoder: 비밀번호 해싱에 사용되는 알고리즘인 BCrypt를 사용하여 비밀번호를 안전하게 저장하고 비교
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}*/


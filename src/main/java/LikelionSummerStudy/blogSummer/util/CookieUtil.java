package LikelionSummerStudy.blogSummer.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.util.Base64;
import java.util.Optional;


public class CookieUtil {
    //서버가 클라이언트에 쿠키를 설정해서 보내는 메서드
    public static void addCookie(HttpServletResponse response, String name,String value, int maxAge){
        Cookie cookie = new Cookie(name, value); //쿠키의 이름, 값 넣어 초기화
        cookie.setPath("/");  //쿠키의 유효 경로 설정
        cookie.setMaxAge(maxAge); //쿠키의 만료 시간 설정 ex) 3600: 1시간
        response.addCookie(cookie); //응답 객체에 쿠키를 추가해서 클라이언트에게 쿠키를 전송
    }

    //쿠키의 이름을 입력받아 쿠키 삭제
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name){
        Cookie[] cookies = request.getCookies(); //iter + tab
        if(cookies==null){
            return;
        }
        for (Cookie cookie : cookies) {
            if(name.equals(cookie.getName())){
                cookie.setValue(""); //쿠키 값을 빈 문자열로 설정
                cookie.setPath("/"); //쿠키를 삭제하려면 설정할 때와 같은 경로를 지정해야 함
                cookie.setMaxAge(0); //0초로 설정하면 브라우저는 이 쿠키를 즉시 삭제
                response.addCookie(cookie); //변경된 쿠키를 응답에 포함해서 브라우저에 다시 보냄
            }
        }
    }
    // 쿠키 배열에서 특정 이름의 쿠키를 Optional로 반환하는 메서드
    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * SerializationUtils.serialize(obj): 자바 객체를 byte[]로 직렬화
     * Base64.getUrlEncoder().encodeToString(...): byte[]->문자열로 인코딩
     * */
    //Java 객체를 직렬화해 문자열로 바꾸고, 쿠키에 저장 가능한 형식으로 변환
    public static String serialize(Object obj){
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    /**
     * cls: 복원할 클래스의 타입 정보
     * cookie.getValue(): 쿠키에 저장된 Base64 문자열을 가져옴
     * Base64.getUrlDecoder().decode(cookie.getValue()): 문자열을 다시 byte[]로 디코딩
     * SerializationUtils.deserialize(...): byte[] 다시 객체로 복원
     * cls.cast(...): 복원된 객체를 T 타입으로 형 변환
     * */

    //쿠키에 문자열로 저장했던 직렬화된 객체를 다시 자바 객체로 되돌리는 함수
    public static <T> T deserialize(Cookie cookie, Class<T> cls){
        return cls.cast(
                SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(cookie.getValue())
                )
        );
    }
    /**
     * 제너릭 문법 <T> 설명
     * <T>는 **타입 매개변수(type parameter)**를 의미
     * 아무 타입 T든 받아서, 같은 타입 T로 반환하겠다라는 의미
     * */
}

package LikelionSummerStudy.blogSummer.config.oauth;

import LikelionSummerStudy.blogSummer.domain.User;
import LikelionSummerStudy.blogSummer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
//DefaultOAuth2UserService: Spring Security OAuth2에서 OAuth2 인증 서버(Google, Naver 등)로부터 사용자 정보를 가져오는 기본 구현 클래스
public class OAuth2UserCustomService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //요청을 바탕으로 유저 정보를 담은 객체를 반환
        OAuth2User user = super.loadUser(userRequest); //구글에서 유저의 정보를 fetch(받아옴)
        saveOrUpdate(user); //유저 정보를 DB에 저장 or update
        return user; //최종 사용자 정보 반환(나중에 SecurityContext에 저장됨)
    }

    private User saveOrUpdate(OAuth2User oAuth2User){
        //oAuth2User.getAttributes(): 구글에서 받은 유저 정보 JSON을 Map<String,Object> 형태로 return
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        //DB에 해당 이메일이 존재->이름만 업데이트 후 반환
        //존재 X->새 유저를 만들어 저장 후 반환
        User user = userRepository.findByEmail(email)
                .map(entity -> entity.update(name))
                .orElse(User.builder()
                        .email(email)
                        .nickname(name)
                        .build()
                );
        return userRepository.save(user);
    }
}

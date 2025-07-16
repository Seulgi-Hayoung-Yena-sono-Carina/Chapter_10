package LikelionSummerStudy.blogSummer.config.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
//application.yml/properties 파일에 있는 jwt 접두사로 시작하는 속성값들을 이 클래스에 바인딩
@ConfigurationProperties("jwt")

public class JwtProperties {
    private String issuer;
    private String secretKey;
}

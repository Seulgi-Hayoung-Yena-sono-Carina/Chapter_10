package LikelionSummerStudy.blogSummer.service;


import LikelionSummerStudy.blogSummer.domain.User;
import LikelionSummerStudy.blogSummer.dto.request.AddUserRequest;
import LikelionSummerStudy.blogSummer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public Long save(AddUserRequest dto){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(encoder.encode(dto.getPassword())) //사용자가 입력한 비밀번호를 BCrypt로 암호화
                .build()).getId(); //암호화된 비번 그리고 이메일만 설정해서 User 객체 생성, DB에 User 저장하고 저장된 User의 id(PK) 반환
    }
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }
}

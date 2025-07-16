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
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Long save(AddUserRequest dto){
        return userRepository.save(User.builder()
                .email(dto.getEmail())
                //패스워드를 저장할 때 시큐리티를 설정하여 패스워드 인코딩용으로 등록한 빈을 사용해서 암호화한 후 저장
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .build()).getId();
    }
    public User findById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("Unexpected user"));
    }
}

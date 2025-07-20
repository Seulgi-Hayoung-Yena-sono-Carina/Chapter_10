
package LikelionSummerStudy.blogSummer.repository;

import LikelionSummerStudy.blogSummer.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    //findById()는 이미 기본으로 제공
    Optional<User> findByEmail(String email); //email로 사용자의 정보 가져오기
}

package LikelionSummerStudy.blogSummer;
import LikelionSummerStudy.blogSummer.domain.Article;
import LikelionSummerStudy.blogSummer.repository.BlogRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;


@Component
@RequiredArgsConstructor
public class TestDataInit {

    private final BlogRepository blogRepository;

    @PostConstruct
    public void init() {
        // 3개의 Article 객체 생성 (시간 설정 생략!)
        Article article1 = new Article("제목 1", "내용 1","유저 1");
        Article article2 = new Article("제목 2", "내용 2", "유저 2");
        Article article3 = new Article("제목 3", "내용 3", "유저 3");

        blogRepository.saveAll(List.of(article1, article2, article3));
    }
}
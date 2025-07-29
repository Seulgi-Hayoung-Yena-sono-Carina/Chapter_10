package LikelionSummerStudy.blogSummer.controller;

import LikelionSummerStudy.blogSummer.domain.Article;
import LikelionSummerStudy.blogSummer.dto.request.AddArticleRequest;
import LikelionSummerStudy.blogSummer.dto.request.UpdateArticleRequest;
import LikelionSummerStudy.blogSummer.dto.response.ArticleResponse;
import LikelionSummerStudy.blogSummer.service.BlogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/articles")
public class BlogApiController {

    private final BlogService blogService;


    /**
     * Principal: Spring Security에서 현재 로그인한 사용자 정보를 담고 있는 객체
     * 로그인한 사용자의 기본 정보(보통 username 또는 user ID)만 포함
     * 민감한 정보인 **password**나 추가적인 nickname 같은 비인증 정보는 포함 X
     * */

    @PostMapping
    //@RequestBody: 클라이언트가 보낸 JSON 데이터를 우리가 지정한 DTO 클래스에 자동으로 매핑
    public ResponseEntity<ArticleResponse> addArticle(@RequestBody AddArticleRequest request,
                                                      Principal principal){
        // Principal에서 이름을 로그로 출력
        String userName = principal.getName();
        log.info("Logged in user: {}", userName);  // 이메일 주소 반환

        // Article 저장 로직
        Article savedArticle = blogService.save(request, userName);

        // Response 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(new ArticleResponse(savedArticle));
    }

    @GetMapping
    public ResponseEntity<List<ArticleResponse>> findAllArticles(){
        List<Article> articles = blogService.findAll();
        List<ArticleResponse> articleResponse = articles.stream()
                .map(ArticleResponse::new)  // Article을 ArticleResponse로 변환
                .toList();
        return ResponseEntity.ok().body(articleResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable("id") Long id){
        Article article = blogService.findById(id);

        return ResponseEntity.ok().body(new ArticleResponse(article));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponse> updateArticle(@PathVariable("id") long id,
                                                         @RequestBody UpdateArticleRequest request){
        Article updatedArticle = blogService.update(id, request);
        return ResponseEntity.ok().body(new ArticleResponse(updatedArticle));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ArticleResponse> patchArticle(@PathVariable("id") long id,
                                                        @RequestBody UpdateArticleRequest request) {
        Article updatedArticle = blogService.patch(id, request);
        return ResponseEntity.ok().body(new ArticleResponse(updatedArticle));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable("id") Long id){
        blogService.delete(id);
        return ResponseEntity.ok().build();
    }

}
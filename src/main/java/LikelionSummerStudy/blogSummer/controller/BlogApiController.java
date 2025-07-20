package LikelionSummerStudy.blogSummer.controller;

import LikelionSummerStudy.blogSummer.domain.Article;
import LikelionSummerStudy.blogSummer.dto.request.AddArticleRequest;
import LikelionSummerStudy.blogSummer.dto.request.UpdateArticleRequest;
import LikelionSummerStudy.blogSummer.dto.response.ArticleResponse;
import LikelionSummerStudy.blogSummer.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/articles")
public class BlogApiController {

    private final BlogService blogService;

    /**
     * @RequestBody: 클라이언트가 보낸 JSON 데이터를 우리가 지정한 DTO 클래스에 자동으로 매핑
     */
    @PostMapping
    public ResponseEntity<ArticleResponse> addArticle(@RequestBody AddArticleRequest request,
                                                      Principal principal){
        Article savedArticle = blogService.save(request, principal.getName());
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
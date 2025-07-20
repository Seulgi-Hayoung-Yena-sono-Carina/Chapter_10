/*
package LikelionSummerStudy.blogSummer.controller;
import LikelionSummerStudy.blogSummer.domain.Article;
import LikelionSummerStudy.blogSummer.dto.request.AddArticleRequest;
import LikelionSummerStudy.blogSummer.dto.request.UpdateArticleRequest;
import LikelionSummerStudy.blogSummer.dto.response.ApiResponse;
import LikelionSummerStudy.blogSummer.dto.response.ArticleResponse;
import LikelionSummerStudy.blogSummer.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


*/
/**
* API 반환 예쁘게 다듬은 버전
**//*


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/articles/v2")
public class BlogApiControllerV2 {

    private final BlogService blogService;

    @PostMapping
    public ResponseEntity<ApiResponse<ArticleResponse>> addArticle(@RequestBody AddArticleRequest request) {
        Article savedArticle = blogService.save(request);
        ArticleResponse articleResponse = new ArticleResponse(savedArticle);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, HttpStatus.CREATED.value(), "게시글 등록 성공", articleResponse));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ArticleResponse>>> findAllArticles() {
        // 서비스에서 Article 목록을 가져옴
        List<Article> articles = blogService.findAll();

        // Article 목록을 ArticleResponse 목록으로 변환
        List<ArticleResponse> articleResponses = articles.stream()
                .map(ArticleResponse::new)  // Article을 ArticleResponse로 변환
                .toList(); // 변환된 리스트를 반환

        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "게시글 조회 성공", articles.stream().map(ArticleResponse::new).toList()));

    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleResponse>> findArticle(@PathVariable("id") Long id) {
        Article article = blogService.findById(id);
        ArticleResponse articleResponse = new ArticleResponse(article);
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "게시글 조회 성공", articleResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleResponse>> updateArticle(@PathVariable("id") long id,
                                                                      @RequestBody UpdateArticleRequest request) {
        Article updatedArticle = blogService.update(id, request);
        ArticleResponse articleResponse = new ArticleResponse(updatedArticle);
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "게시글 수정 성공", articleResponse));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleResponse>> patchArticle(@PathVariable("id") long id,
                                                                     @RequestBody UpdateArticleRequest request) {
        Article updatedArticle = blogService.patch(id, request);
        ArticleResponse articleResponse = new ArticleResponse(updatedArticle);
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "게시글 부분 수정 성공", articleResponse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteArticle(@PathVariable("id") Long id) {
        blogService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "게시글 삭제 성공", null));
    }
}*/

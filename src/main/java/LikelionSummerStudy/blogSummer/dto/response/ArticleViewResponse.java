package LikelionSummerStudy.blogSummer.dto.response;

import LikelionSummerStudy.blogSummer.domain.Article;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class ArticleViewResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String author;

    public ArticleViewResponse(Article article){
        this.id=article.getId();
        this.title= article.getTitle();
        this.content=article.getContent();
        this.createdAt=article.getCreatedAt();
        this.author=article.getAuthor();
    }
}

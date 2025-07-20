package LikelionSummerStudy.blogSummer.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Optional;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Data
@NoArgsConstructor(access= AccessLevel.PROTECTED) //protected type 기본 생성자
@AllArgsConstructor
//@Builder //모든 필드(id,createdAt까지 다) 포함하는 객체 생성
public class Article {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id",updatable=false)
    private Long id;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="content", nullable = false)
    private String content;


    @Column(name="author",nullable = false)
    private String author;

    @CreatedDate
    @Column(name="created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name="updated_at")
    private LocalDateTime updatedAt;


    @Builder //일부 필드만 포함한 빌더 생성자를 명시적으로 정의
    public Article(String title, String content,String author){
        this.title=title;
        this.content=content;
        this.author=author;
    }

    public void update(String title, String content){
        this.title=title;
        this.content=content;
    }

    public void update(Optional<String> title, Optional<String> content) {

        //ifPresent: Optional.empty()라면 아무것도 () 안의 코드 수행 X

        title.ifPresent(t -> this.title = t);  // title이 제공되면 업데이트
        content.ifPresent(c -> this.content = c);  // content가 제공되면 업데이트
    }
}

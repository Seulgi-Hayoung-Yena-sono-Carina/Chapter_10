/*
package LikelionSummerStudy.blogSummer.controller;

import LikelionSummerStudy.blogSummer.domain.Article;
import LikelionSummerStudy.blogSummer.dto.request.AddArticleRequest;
import LikelionSummerStudy.blogSummer.dto.request.UpdateArticleRequest;
import LikelionSummerStudy.blogSummer.repository.BlogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.linesOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


*/
/**
 * @Disabled 애노테이션을 활용해서 V2Test 비활성화
 * V2 버전 사용을 위해서는 V1에 @Disabled("V1 has been disabled") 붙이고 V2의 @Disabled 삭제
 **//*

@Disabled("V2 has been disabled")
@SpringBootTest
@AutoConfigureMockMvc //MockMvc를 자동 구성
public class BlogApiControllerV2Test {

    //HTTP 요청을 보내고 응답을 테스트할 수 있게 해주는 도구
    @Autowired
    protected MockMvc mockMvc;

    //자바 객체를 JSON 문자열로 변환 (장고 Serializer)
    @Autowired
    protected ObjectMapper objectMapper;

    //Spring Web 애플리케이션 컨텍스트
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private BlogRepository blogRepository;

    @BeforeEach
    public void setUp() {
        //mockMvc를 수동으로 설정
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        //테스트 전 DB를 비우기
        blogRepository.deleteAll();
    }

    @DisplayName("addArticle Test")
    @Test
    public void addArticle() throws Exception {
        // given
        final String url = "/api/articles/v2";
        final String title = "title";
        final String content = "content";
        final AddArticleRequest userRequest = new AddArticleRequest(title, content);
        //userRequest 객체를 JSON 문자열로 바꾸기
        final String requestBody = objectMapper.writeValueAsString(userRequest);

        // when
        //POST /api/articles 요청을 JSON 형식으로 보내기
        //requestBody는 {"title":"title","content":"content"} 같은 형태로
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        result.andExpect(status().isCreated()); // HTTP 201 Created 기대
        List<Article> articles = blogRepository.findAll();
        assertThat(articles).hasSize(1);
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        assertThat(articles.get(0).getContent()).isEqualTo(content);
    }

    @DisplayName("findAllArticles Test")
    @Test
    public void findAllArticles() throws Exception {
        // given
        final String url = "/api/articles/v2";
        final String title = "title";
        final String content = "content";

        // Article 하나를 DB에 직접 저장
        blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        // when
        // GET /api/articles 요청을 보내기
        final ResultActions resultActions = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                // response의 data 필드 내부의 첫 번째 요소 검증
                .andExpect(jsonPath("$.data[0].content").value(content))
                .andExpect(jsonPath("$.data[0].title").value(title));
    }

    @DisplayName("findArticle Test")
    @Test
    public void findArticle() throws Exception {
        // given
        final String url = "/api/articles/v2/{id}";
        final String title = "title";
        final String content = "content";

        // Article 하나를 DB에 직접 저장
        Article savedArticle = blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        // when
        ResultActions resultActions = mockMvc.perform(get(url, savedArticle.getId()));

        // then
        resultActions
                .andExpect(status().isOk())
                // response의 data 필드 내부의 content와 title 검증
                .andExpect(jsonPath("$.data.content").value(content))
                .andExpect(jsonPath("$.data.title").value(title));
    }

    @DisplayName("deleteArticle Test")
    @Test
    public void deleteArticle() throws Exception {
        //given
        final String url = "/api/articles/v2/{id}";
        final String title = "title";
        final String content = "content";

        //Article 하나를 DB에 직접 저장
        Article savedArticle = blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        //when
        mockMvc.perform(delete(url, savedArticle.getId()))
                .andExpect(status().isOk());

        //then
        List<Article> articles = blogRepository.findAll();

        assertThat(articles).isEmpty();
    }

    @DisplayName("updateArticle Test")
    @Test
    public void updateArticle() throws Exception {
        //given
        final String url = "/api/articles/v2/{id}";
        final String title = "title";
        final String content = "content";

        //Article 하나를 DB에 직접 저장
        Article savedArticle = blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        final String newTitle="new title";
        final String newContent="new content";

        UpdateArticleRequest request = new UpdateArticleRequest(newTitle, newContent);

        //when
        ResultActions result = mockMvc.perform(put(url, savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)));

        //then
        result.andExpect(status().isOk());
        Article article = blogRepository.findById(savedArticle.getId()).get();

        assertThat(article.getTitle()).isEqualTo(newTitle);
        assertThat(article.getContent()).isEqualTo(newContent);

    }
}*/

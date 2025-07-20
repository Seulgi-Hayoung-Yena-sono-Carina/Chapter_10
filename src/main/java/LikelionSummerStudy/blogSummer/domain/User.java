package LikelionSummerStudy.blogSummer.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Table(name="users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@Entity
//UserDetails: 스프링 시큐리티에서 사용자의 인증 정보를 담아두는 인터페이스
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id",updatable = false)
    private Long id;

    @Column(name="email",nullable = false,unique = true)
    private String email;

    @Column(name="password")
    private String password;

    @Column(name="nickname", unique = true)
    private String nickname;

    @Builder
    public User(String email, String password, String nickname){
        this.email=email;
        this.password=password;
        this.nickname=nickname;
    }

    //사용자가 갖고 있는 권한 목록 반환
    /**
     * GrantedAuthority: Spring Security에서 사용자의 권한(role)을 표현
     * getAuthorities(): 현재 로그인된 사용자의 권한 목록을 반환하는 메서드
     * */

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //SimpleGrantedAuthority: Spring Security에서 권한(Role이나 Authority) 을 표현할 때 사용하는 클래스
        return List.of(new SimpleGrantedAuthority("user"));
    }

    //사용자 비번 반환
    @Override
    public String getPassword() {
        return password;
    }

    //사용자 이름 반환
    @Override
    public String getUsername() {
        return email;
    }

    //계정이 만료되지 않으면 true를 반환
    @Override
    public boolean isAccountNonExpired() {
        //만료되었는지 확인하는 로직
        return true;
    }

    //계정이 잠금되었는지 확인
    @Override
    public boolean isAccountNonLocked() {
        //계정 잠금되었는지 확인하는 로직
        return true;
    }

    //비밀번호가 만료되었는지 확인
    @Override
    public boolean isCredentialsNonExpired() {
        //패스워드가 만료되었는지 확인하는 로직
        return true;
    }

    //계정이 사용 가능한지 확인
    @Override
    public boolean isEnabled() {
        //계정이 사용 가능한지 확인하는 로직
        return true;
    }

    public User update(String nickname){
        this.nickname=nickname;
        return this;
    }
}

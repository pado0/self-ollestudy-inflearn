package com.pado.ollestudy.domain;

import lombok.*;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

// Todo : 백기선님 rest api 강의 듣기
@Entity
@Getter @Setter
@EqualsAndHashCode(of = "id") // id에만 적용. 연관관계가 복잡해질 때 서로 다른 연관관계를 순환참조하느라 무한루프가 발생할 수 있어 id에만 준다.
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {

    @Id @GeneratedValue
    private Long id;

    // email과 nickname으로 모두 로그인 가능. 둘 다 유일해야한다. unique.
    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    // 검증 완료된 이메일인지 체크
    private boolean emailVerified;

    // 이메일 검증시 사용할 토큰값
    private String emailCheckToken;

    // 가입 날짜
    private LocalDateTime joinedAt;

    // 자기소개
    private String bio;

    // 자신의 웹사이트 url
    private String url;

    // 직업
    private String occupation;

    // 거주지
    private String location;

    // 프로필 이미지
    // String은 varchar(255)로 기본 매핑되는데, @Lob을 쓰면 더 길게할 수 있음
    @Lob //texttype으로 db 매핑
    @Basic(fetch = FetchType.EAGER) // 프로필이미지는 그때그때 가져오도록
    private String profileImage;


    /* 알림 관련 내용들 */
    // 스터기가 만들어진걸 메일로 알림 받을 것인가
    private boolean studyCreatedByEmail;

    // 스터디가 만들어진걸 웹으로 받을 것인가
    private boolean studyCreatedByWeb;

    // 스터디에 가입신청 결과를 이메일로 받을것인가 ...
    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb;

    private boolean studyUpdatedByEmail;

    private boolean studyUpdatedByWeb;

    // 이메일 인증 토큰 생성
    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
    }
}

# 웹툰 서비스 (WWW - Wow! Welcome Webtoon)

목차


  - [Repository 설명](#repository-설명)
  - [프로젝트 설명](#프로젝트-설명)
  - [기술 스택](#기술-스택)
  - [아키텍처](#아키텍처)
  - [데이터베이스](#데이터베이스)
  - [API 문서](#api-문서)
  - [프로젝트 개선 내용 정리](#프로젝트-개선-내용-정리)
    - [개선 내용](#개선-내용)
    - [추가된 내용](#추가된-내용)

---

## Repository 설명
![main](./etc/main.png)

> 기존에 스마일게이트 온라인 서버개발캠프에서 두 달간 (20.01~20.03) 진행했던<b> [프로젝트](https://github.com/Smilegate-WWW/wow-welcome-webtoon)</b>를 fork 하여
>
> 공부하면서 개선하고 싶었던 점들을 개선 중인 Repository입니다.

</br>

---

## 프로젝트 설명

> 유저가 자신의 웹툰을 등록하거나 다른 사람의 웹툰을 볼 수 있는 서비스입니다.
>
> 웹툰을 보고 별점을 매길 수 있습니다.
>
> 댓글을 달 수 있고 댓글에 좋아요 혹은 싫어요를 요청할 수 있습니다.

</br>

---

## 기술 스택

|                        기존 프로젝트 기술 스택                        |          추가된 기술스택          |
| :- | :- |
| Java 8 </br> Spring Boot 2.2.x </br> &ensp;- Spring Data JPA </br> &ensp;- Spring Security </br> MySQL </br> Redis </br> Nginx </br> React | Junit5, AssertJ </br> Spring Rest Docs |

</br>

---

## 아키텍처

![Architecture](./etc/architecture.png)

</br>

---

## 데이터베이스

![Database](./etc/db.png)

</br>

---

## API 문서

Spring Rest Docs으로 모두 전환되면 링크 추가 예정

<details>
<summary>이전에 Postman으로 작성했던 API 문서 링크(더 보기)</summary>
<div markdown="1">

- [인증서버 API](https://documenter.getpostman.com/view/10215521/SWTK3Dqq) </br>
- [파일서버 API](https://documenter.getpostman.com/view/10254430/SzKSRygn) </br>
- [플랫폼 서버 API](https://documenter.getpostman.com/view/9773992/SzKWtGnX) </br>

</div>
</details>





</br>

---

## 프로젝트 개선 내용 정리

### 개선 내용

| No | 기존 프로젝트 방식 | 개선 내용 | 참고 |
| :-- | :- | :- | :- |
| 1 | Test Code 작성 X | Junit5 + AssertJ, Mockito를 이용해 Unit Test 작성 | [#6](https://github.com/momojh94/wow-welcome-webtoon/pull/6) [#10](https://github.com/momojh94/wow-welcome-webtoon/pull/10) |
| 2 | Postman을 이용해 수동으로 API 문서화 | API 문서 자동화 도구인 Spring Rest Docs 도입 |  [#16](https://github.com/momojh94/wow-welcome-webtoon/pull/16) |
| 3 | 일관성 없는 Code Case Style | Java Code는 Camel Case로 수정, API 통신할 때 Json Data는 설정 값을 바꿔서 그대로 Snake Case로 사용 | [#22](https://github.com/momojh94/wow-welcome-webtoon/pull/22) |
| 4 | 성별, 웹툰 장르 등 상태 값을 가지는 Column을 0, 1, 2와 같이 DB Code 값을 그대로 사용하여 비직관적인 개발 | 상태 값을 가지는 Column을 Enum으로 전환 및 Generic한 Enum Converter 적용  | [#22](https://github.com/momojh94/wow-welcome-webtoon/pull/22) | 

</br>

### 추가된 내용

| No | 내용 | 참고 |
| :-: | :-: | :-: |
| 1 | JaCoCo 적용 - Code Coverage 확인 용도 | [#8](https://github.com/momojh94/wow-welcome-webtoon/pull/8) |

</br>

---

[맨 위로 가기](#top)




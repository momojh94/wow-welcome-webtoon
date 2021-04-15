# 웹툰 서비스

### 기존에 했던 [프로젝트](https://github.com/Smilegate-WWW/wow-welcome-webtoon) 에서 공부하면서 개선 하고 싶었던 것을 작업 중인 저장소 입니다.

![main](./etc/main.png)

> 유저가 자신의 웹툰을 등록하거나 다른 사람의 웹툰을 볼 수 있는 서비스입니다
> 
> 웹툰을 보고 별점을 매길 수 있습니다.
> 
> 댓글을 달 수 있고 댓글에 좋아요 혹은 싫어요를 요청할 수 있습니다.

<br/>

---
## 프로젝트 기술스택
- Java 8
- Spring Boot 2.2.x
- Spring Data JPA
- Spring Security
- MySQL 8.0
- Redis
- Nginx
- Junit5, AssertJ
- React
---

## Architecture

![Architecture](./etc/architecture.png)

---

## Database

![Database](./etc/db.png)

---

## API 문서

>- [인증서버 API](https://documenter.getpostman.com/view/10215521/SWTK3Dqq) </br>
>- [파일서버 API](https://documenter.getpostman.com/view/10254430/SzKSRygn) </br>
>- [플랫폼 서버 API](https://documenter.getpostman.com/view/9773992/SzKWtGnX) </br>

---

## File Structure

```
├── backend
│   └── core
│   └── auth_server
│   └── file_server
│   └── platform_server
└── frontend
```

[맨 위로 가기](#top)
</br>

---

## 추가 작업

Service Layer 단위 테스트 추가 
  - Platform 모듈 서비스 레이어 단위 테스트 추가 완료, Auth 및 File 모듈 단위 테스트 추가 예정

JaCoCo 적용 [#7](https://github.com/momojh94/wow-welcome-webtoon/issues/7)
  - 코드 커버리지 확인을 위해 적용

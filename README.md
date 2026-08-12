# FinSight

> 관심 기업의 뉴스·활동을 자동으로 수집하고 AI가 요약·분류해 "요즘 이 회사 동향"으로 보여주는 기업분석 큐레이션 서비스
>

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F)
![MyBatis](https://img.shields.io/badge/MyBatis-4.1.0-red)
![MySQL](https://img.shields.io/badge/MySQL-8.4-4479A1)
![Build](https://img.shields.io/badge/build-Gradle-02303A)

---

## 프로젝트 소개

취업 준비 과정에서 기업분석(지원동기·면접 대비)을 할 때, 관심 기업의 최신 뉴스를 일일이 검색하고 정리하는 수고가 크다. **FinSight는 그 과정을 자동화한다.** 관심 기업을 등록하면 해당 회사의 뉴스를 주기적으로 수집하고, AI가 요약·분류해 한눈에 볼 수 있게 제공한다.

- **합법적 데이터 파이프라인** — 직접 크롤링 대신 공식 API(NAVER API HUB)를 사용하고, 기사 본문은 저장하지 않으며 출처·원문 링크로 연결한다.
- **재실행 안전성(멱등성)** — 같은 기사를 여러 번 수집해도 중복 저장되지 않도록 URL 정규화 + 해시 + DB 제약으로 방어한다.
- **금융 도메인 지향** — 구독 결제(금액·상태 전이·정합성)를 핵심 축으로 설계하여 단순 CRUD를 넘어선다. *(예정)*

---

## 주요 기능

### 구현 완료
- **뉴스 수집** — NAVER API HUB 뉴스 검색 API 연동 (어댑터 구조로 소스 교체 가능)
- **데이터 정제** — HTML 태그/엔티티 제거, 발행일 KST 정규화, 원문 링크 우선 채택
- **중복 방지 저장** — URL 정규화 → SHA-256 해시 → `UNIQUE` 제약 + `INSERT IGNORE`로 재수집 시 중복 차단
- **스키마 관리** — Flyway 기반 마이그레이션
- **헬스 체크** — Actuator `/actuator/health` (DB 연결 상태 포함)
- **로컬 개발 환경** — Docker Compose 원클릭 기동

### 진행 예정
- 수집 배치화(스케줄러) 및 중복 제거 3계층(해시 → SimHash → 임베딩)
- AI 요약·분류 (메시지 큐 기반 비동기 처리 + 캐시)
- 관심 기업 등록 / 동향 조회 API (커서 페이징)
- 구독 결제 (PG 샌드박스) + 구독 상태 머신 + 재청구 배치
- 웹 클라이언트 (React PWA) 및 실배포·모니터링

---

## 아키텍처

<img width="1191" height="593" alt="image" src="https://github.com/user-attachments/assets/3f3f02cb-c2fc-4d46-b96b-44c40605205a" />


**설계 포인트**
- **어댑터 패턴** — `NewsSource` 인터페이스 뒤에 네이버 구현체를 숨겨, 데이터 소스가 바뀌어도(예: 콘솔 이관) 구현체만 교체하면 된다.
- **DB 레벨 멱등성** — 중복 판정을 애플리케이션이 아닌 DB의 `UNIQUE` 제약으로 처리하여, 동시 수집 상황에서도 안전하다.
- **저작권 통제** — `trend_item` 스키마에 기사 본문 컬럼 자체를 두지 않아, 저작권 리스크를 구조적으로 차단한다.

---

## 기술 스택

| 영역 | 기술 | 선택 이유 |
|---|---|---|
| 런타임 | Spring Boot 4.1.0 / Java 21 | MyBatis 스타터 호환 확인 후 채택한 현재 권장 라인 |
| DB 접근 | MyBatis 4.1.0 | 복잡 조회·전문검색을 SQL로 직접 제어 |
| DB | MySQL 8.4 (ngram FULLTEXT) | 별도 인프라 없이 한국어 전문검색 |
| 스키마 | Flyway | 코드로 스키마 버전 관리 |
| HTTP | RestClient (connect/read 타임아웃) | 외부 장애가 서비스로 전파되지 않도록 격리 |
| 동시성 | Virtual Threads | I/O 대기 지배적 워크로드에 적합 |
| 빌드 | Gradle | — |
| 테스트 | JUnit 5, Testcontainers, AssertJ | 실제 MySQL 기반 통합 테스트 |
| 로컬 인프라 | Docker Compose | MySQL 사용 중 · Redis/RabbitMQ는 예정 단계용으로 예약 |

> 예정 스택: RabbitMQ, Redis, Spring Batch, Resilience4j, React 19(PWA), 토스페이먼츠, Grafana

---

## 프로젝트 구조

```
bizradar/
├─ src/main/java/com/bizradar/
│  ├─ BizradarApplication.java
│  ├─ collect/                    # 뉴스 수집 · 정규화 · 저장
│  │  ├─ NewsSource.java          # 소스 추상화 (어댑터)
│  │  ├─ NaverNewsSource.java     # 네이버 구현체
│  │  ├─ RawArticle.java          # 정제된 기사 (저장 전 단계)
│  │  ├─ UrlNormalizer.java       # URL 정규화 + SHA-256
│  │  ├─ TrendItem.java           # 저장 단위
│  │  ├─ TrendItemMapper.java     # MyBatis (INSERT IGNORE)
│  │  ├─ TrendItemWriter.java     # 저장 흐름 (@Service)
│  │  └─ CompanyMapper.java
│  └─ common/health/DbHealthMapper.java
├─ src/main/resources/
│  ├─ application.properties
│  └─ db/migration/V1__init.sql   # Flyway 스키마
├─ docker-compose.yml             # MySQL · Redis · RabbitMQ
└─ build.gradle
```

---

## 실행 방법

### 사전 요구사항
- JDK 21
- Docker Desktop

### 1. 시크릿 설정
NAVER API HUB에서 검색 API 키를 발급받은 뒤, 프로젝트 루트에 `application-local.properties`를 만든다. *(이 파일은 `.gitignore` 대상 — 커밋되지 않는다)*

```properties
naver.news.client-id=발급받은_Client_ID
naver.news.client-secret=발급받은_Client_Secret
```

### 2. 인프라 기동
```bash
docker compose up -d mysql
```

### 3. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 4. 동작 확인
```bash
curl http://localhost:8080/actuator/health
# {"status":"UP","components":{"db":{"status":"UP", ...}}}
```

`db: UP`이 확인되면 앱이 MySQL에 정상 연결된 것이다.

---

## 개발 로드맵

전체 일정을 압축 진행 중이며, 각 단계마다 "돌아가는 결과물"을 남기는 것을 원칙으로 한다.

| 단계 | 내용 | 상태 |
|---|---|---|
| 뼈대 | 프로젝트 골격 · MyBatis/Flyway/Testcontainers 검증 · 헬스 체크 | ✅ 완료 |
| 수집기 | 네이버 API 연동 · 데이터 정제 (어댑터 구조) | ✅ 완료 |
| 정규화·저장 | URL 정규화 · 해시 기반 중복 방지 저장 | ✅ 완료 |
| 배치·중복 | 수집 스케줄러 · 중복 제거 3계층 | ⏳ 예정 |
| AI | 요약·분류 (비동기 큐 + 캐시) · 품질 가드레일 | ⏳ 예정 |
| 조회·구독 | 동향 조회 API · 관심 기업 · 구독 결제 · 상태 머신 | ⏳ 예정 |
| 클라이언트·운영 | React PWA · DB 튜닝 · 모니터링 · 실배포 | ⏳ 예정 |

---

<sub>본 저장소는 포트폴리오 목적의 개인 프로젝트입니다.</sub>

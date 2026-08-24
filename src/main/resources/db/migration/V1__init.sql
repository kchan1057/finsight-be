-- 기업
CREATE TABLE company (
                         id         BIGINT       NOT NULL AUTO_INCREMENT,
                         name       VARCHAR(100) NOT NULL,
                         created_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                         PRIMARY KEY (id),
                         UNIQUE KEY uk_company_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 기업 검색 키워드 ("회사명 + 키워드"로 뉴스 수집)
CREATE TABLE company_keyword (
                                 id         BIGINT       NOT NULL AUTO_INCREMENT,
                                 company_id BIGINT       NOT NULL,
                                 keyword    VARCHAR(100) NOT NULL,
                                 PRIMARY KEY (id),
                                 UNIQUE KEY uk_company_keyword (company_id, keyword),
                                 CONSTRAINT fk_ck_company FOREIGN KEY (company_id) REFERENCES company(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 회원
CREATE TABLE member (
                        id          BIGINT       NOT NULL AUTO_INCREMENT,
                        email       VARCHAR(255) NOT NULL,
                        provider    VARCHAR(20)  NOT NULL,      -- KAKAO 등
                        provider_id VARCHAR(100) NOT NULL,
                        created_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                        PRIMARY KEY (id),
                        UNIQUE KEY uk_member_provider (provider, provider_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 회원 ↔ 관심기업 (무료 3~5개)
CREATE TABLE member_company (
                                id         BIGINT      NOT NULL AUTO_INCREMENT,
                                member_id  BIGINT      NOT NULL,
                                company_id BIGINT      NOT NULL,
                                created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                PRIMARY KEY (id),
                                UNIQUE KEY uk_member_company (member_id, company_id),
                                CONSTRAINT fk_mc_member  FOREIGN KEY (member_id)  REFERENCES member(id),
                                CONSTRAINT fk_mc_company FOREIGN KEY (company_id) REFERENCES company(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 동향 아이템 (정규화된 뉴스 1건) — 기사 본문 컬럼 없음(저작권 통제를 스키마로 강제)
CREATE TABLE trend_item (
                            id             BIGINT        NOT NULL AUTO_INCREMENT,
                            company_id     BIGINT        NOT NULL,
                            title          VARCHAR(500)  NOT NULL,
                            category       VARCHAR(30)   NULL,          -- 디지털/실적/ESG/인사/신사업 (AI 분류, 초기 NULL)
                            published_at   DATETIME(6)   NOT NULL,      -- 발행일(KST)
                            source         VARCHAR(100)  NOT NULL,      -- 출처
                            origin_url     VARCHAR(1000) NOT NULL,      -- 원문 링크
                            url_hash       CHAR(64)      NOT NULL,      -- 정규화 URL의 SHA-256 (중복 방지)
                            summary        VARCHAR(1000) NULL,          -- AI 재작성 요약 (초기 NULL)
                            summary_status VARCHAR(20)   NOT NULL DEFAULT 'PENDING',  -- PENDING/DONE/FAILED
                            created_at     DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                            PRIMARY KEY (id),
                            UNIQUE KEY uk_trend_url_hash (url_hash),                 -- 같은 기사 재수집 방지(멱등)
                            KEY idx_trend_company_published (company_id, published_at),
                            FULLTEXT KEY ft_trend_title (title) WITH PARSER ngram,   -- 한국어 전문검색
                            CONSTRAINT fk_trend_company FOREIGN KEY (company_id) REFERENCES company(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 수집 로그
CREATE TABLE collect_log (
                             id            BIGINT       NOT NULL AUTO_INCREMENT,
                             company_id    BIGINT       NOT NULL,
                             source        VARCHAR(100) NOT NULL,
                             status        VARCHAR(20)  NOT NULL,        -- SUCCESS/FAILED
                             fetched_count INT          NOT NULL DEFAULT 0,
                             new_count     INT          NOT NULL DEFAULT 0,
                             message       VARCHAR(500) NULL,
                             created_at    DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                             PRIMARY KEY (id),
                             KEY idx_collect_company_created (company_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

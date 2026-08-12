package com.bizradar.collect;

import java.time.OffsetDateTime;

/** 외부 소스에서 가져와 정제한 기사 1건 (아직 DB 저장 전 단계). */
public record RawArticle(
    String title,
    String originalLink,
    String naverLink,
    String description,
    OffsetDateTime publishedAt
) {}

package com.bizradar.collect;

import java.time.LocalDateTime;

/** trend_item 테이블에 저장할 한 건. */
public record TrendItem(
    Long companyId,
    String title,
    LocalDateTime publishedAt, // KST 벽시계 시간
    String source,
    String originUrl,
    String urlHash
) {}
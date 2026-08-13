package com.bizradar.trend;

import com.bizradar.collect.RawArticle;
import com.bizradar.common.UrlNormalizer;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrendItemWriter {

  private final TrendItemMapper trendItemMapper;

  public int saveNew(long companyId, String source, List<RawArticle> articles) {
    int newCount = 0;
    for (RawArticle a : articles) {
      String urlHash = UrlNormalizer.urlHash(a.originalLink());
      TrendItem item = new TrendItem(
          companyId,
          a.title(),
          a.publishedAt().toLocalDateTime(),  // KST 벽시계
          source,
          a.originalLink(),
          urlHash
      );
      newCount += trendItemMapper.insertIgnore(item);   // 1 또는 0 누적
    }
    return newCount;
  }
}

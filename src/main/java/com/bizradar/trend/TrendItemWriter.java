package com.bizradar.trend;

import com.bizradar.collect.RawArticle;
import com.bizradar.common.SimHash;
import com.bizradar.common.UrlNormalizer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TrendItemWriter {

  private static final int SIMILAR_THRESHOLD = 3;   // 해밍거리 3 이하면 같은 기사 (Google 2007)
  private static final int RECENT_DAYS = 3;         // 최근 3일치와만 비교 (후보 축소)

  private final TrendItemMapper trendItemMapper;

  public TrendItemWriter(TrendItemMapper trendItemMapper) {
    this.trendItemMapper = trendItemMapper;
  }

  public int saveNew(long companyId, String source, List<RawArticle> articles) {
    // 비교 후보: 같은 기업의 최근 3일치 지문
    List<Long> knownHashes = new ArrayList<>(
        trendItemMapper.findRecentSimhashes(companyId, RECENT_DAYS));

    int newCount = 0;
    for (RawArticle a : articles) {
      long simhash = SimHash.of(a.title());              // 2계층: 제목 지문

      if (isSimilarToKnown(simhash, knownHashes)) {
        continue;                                       // 비슷한 게 이미 있음 → 건너뜀
      }

      TrendItem item = new TrendItem(
          companyId,
          a.title(),
          a.publishedAt().toLocalDateTime(),
          source,
          a.originalLink(),
          UrlNormalizer.urlHash(a.originalLink()),    // 1계층: URL 지문
          simhash
      );

      int inserted = trendItemMapper.insertIgnore(item);  // DB가 url_hash로 최종 방어
      if (inserted > 0) {
        newCount++;
        knownHashes.add(simhash);   // 같은 배치 안의 후속 기사도 이것과 비교되게
      }
    }
    return newCount;
  }

  /** 알려진 지문 중 해밍거리 임계값 이내가 있으면 true. */
  private boolean isSimilarToKnown(long simhash, List<Long> knownHashes) {
    for (Long known : knownHashes) {
      if (SimHash.hammingDistance(simhash, known) <= SIMILAR_THRESHOLD) {
        return true;
      }
    }
    return false;
  }
}

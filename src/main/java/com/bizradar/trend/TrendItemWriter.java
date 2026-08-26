package com.bizradar.trend;

import com.bizradar.collect.RawArticle;
import com.bizradar.common.TitleSimilarity;
import com.bizradar.common.UrlNormalizer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class TrendItemWriter {

  /** 자카드 유사도 임계값 — 실측으로 결정.
   *  같은 사건 0.342~0.750 / 다른 사건 0.093~0.125 → 두 분포 중간값 채택 */
  private static final double SIMILARITY_THRESHOLD = 0.25;
  private static final int RECENT_DAYS = 3;

  private final TrendItemMapper trendItemMapper;

  public TrendItemWriter(TrendItemMapper trendItemMapper) {
    this.trendItemMapper = trendItemMapper;
  }

  public int saveNew(long companyId, String source, List<RawArticle> articles) {
    // 비교 후보: 같은 기업의 최근 3일치 제목 (토큰 집합으로 미리 변환해 재사용)
    List<Set<String>> knownTokens = new ArrayList<>();
    for (String title : trendItemMapper.findRecentTitles(companyId, RECENT_DAYS)) {
      knownTokens.add(TitleSimilarity.tokenize(title));
    }

    int newCount = 0;
    for (RawArticle a : articles) {
      Set<String> tokens = TitleSimilarity.tokenize(a.title());

      if (isSimilarToKnown(tokens, knownTokens)) {
        continue;                       // 같은 사건 기사가 이미 있음 → 건너뜀
      }

      TrendItem item = new TrendItem(
          companyId,
          a.title(),
          a.publishedAt().toLocalDateTime(),
          source,
          a.originalLink(),
          UrlNormalizer.urlHash(a.originalLink()),   // 1계층: URL 완전 동일
          null                                       // simhash 컬럼은 미사용(이력 보존)
      );

      int inserted = trendItemMapper.insertIgnore(item);
      if (inserted > 0) {
        newCount++;
        knownTokens.add(tokens);        // 같은 배치 내 후속 기사도 비교되게
      }
    }
    return newCount;
  }

  /** 알려진 제목 중 임계값 이상 유사한 것이 있으면 true. */
  private boolean isSimilarToKnown(Set<String> tokens, List<Set<String>> knownTokens) {
    for (Set<String> known : knownTokens) {
      if (TitleSimilarity.jaccard(tokens, known) >= SIMILARITY_THRESHOLD) {
        return true;
      }
    }
    return false;
  }
}

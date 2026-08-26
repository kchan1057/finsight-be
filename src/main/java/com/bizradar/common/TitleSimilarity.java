package com.bizradar.common;

import java.util.HashSet;
import java.util.Set;

/** 제목 유사도(자카드). 짧은 텍스트에 적합 — 2-gram 토큰 집합의 겹침 비율로 판정. */
public final class TitleSimilarity {

  private TitleSimilarity() {}

  /** 제목을 2-gram 토큰 집합으로 변환. */
  public static Set<String> tokenize(String title) {
    Set<String> tokens = new HashSet<>();
    if (title == null) return tokens;

    String cleaned = title.toLowerCase().replaceAll("[^0-9a-z가-힣]", "");
    for (int i = 0; i < cleaned.length() - 1; i++) {
      tokens.add(cleaned.substring(i, i + 2));
    }
    return tokens;
  }

  /** 자카드 유사도 = 교집합 / 합집합 (0.0 ~ 1.0, 클수록 비슷) */
  public static double jaccard(Set<String> a, Set<String> b) {
    if (a.isEmpty() || b.isEmpty()) return 0.0;

    Set<String> intersection = new HashSet<>(a);
    intersection.retainAll(b);              // 양쪽에 다 있는 것만 남김

    Set<String> union = new HashSet<>(a);
    union.addAll(b);                        // 양쪽 합침 (중복은 자동 제거)

    return (double) intersection.size() / union.size();
  }

  /** 제목 두 개를 바로 비교. */
  public static double similarity(String titleA, String titleB) {
    return jaccard(tokenize(titleA), tokenize(titleB));
  }
}

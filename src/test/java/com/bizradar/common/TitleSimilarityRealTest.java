package com.bizradar.common;

import org.junit.jupiter.api.Test;

class TitleSimilarityRealTest {

  @Test
  void 실제_제목들의_유사도_분포() {
    // 같은 사건 그룹 A: 모바일 신분증
    String[] groupA = {
        "카카오뱅크, 출시 1년 '모바일 신분증' 70만장 발급",
        "지갑 없이 은행·병원·공항까지…카카오뱅크 모바일 신분증 70만장 돌파",
        "카톡에서도 꺼내 쓴다…카카오뱅크 모바일 신분증 70만장 돌파",
        "카카오뱅크 '모바일 신분증' 70만장 발급…출시 1년여 만",
        "카카오뱅크, '모바일 신분증' 출시 1년 만에 약 70만장 발급"
    };
    // 같은 사건 그룹 B: 노조 파업
    String[] groupB = {
        "카카오뱅크 노조, 다음주 닷새간 전면 총파업 예고",
        "카카오뱅크 노조, 31일부터 닷새 총파업 예고",
        "카카오뱅크 노조 5일 연속 전면파업 예고"
    };

    System.out.println("\n===== 같은 사건 A (모바일 신분증) =====");
    printPairs(groupA, groupA, true);

    System.out.println("\n===== 같은 사건 B (노조 파업) =====");
    printPairs(groupB, groupB, true);

    System.out.println("\n===== 다른 사건 (A vs B) =====");
    printPairs(groupA, groupB, false);
    System.out.println();
  }

  private void printPairs(String[] x, String[] y, boolean sameArray) {
    double min = 1.0, max = 0.0;
    for (int i = 0; i < x.length; i++) {
      int start = sameArray ? i + 1 : 0;
      for (int j = start; j < y.length; j++) {
        double s = TitleSimilarity.similarity(x[i], y[j]);
        min = Math.min(min, s);
        max = Math.max(max, s);
        System.out.printf("  %.3f : %s%n          %s%n", s, x[i], y[j]);
      }
    }
    System.out.printf("  → 범위: %.3f ~ %.3f%n", min, max);
  }
}

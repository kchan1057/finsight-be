package com.bizradar.common;

import org.junit.jupiter.api.Test;

class SimHashRealTest {

  @Test
  void 실제_기사_제목들의_거리() {
    String[] titles = {
        "카카오뱅크, 출시 1년 '모바일 신분증' 70만장 발급",
        "지갑 없이 은행·병원·공항까지…카카오뱅크 모바일 신분증 70만장 돌파",
        "카톡에서도 꺼내 쓴다…카카오뱅크 모바일 신분증 70만장 돌파",
        "카카오뱅크 '모바일 신분증' 70만장 발급…출시 1년여 만",
        "카카오뱅크, '모바일 신분증' 출시 1년 만에 약 70만장 발급",
        "카카오뱅크 노조, 다음주 닷새간 전면 총파업 예고",
        "카카오뱅크 노조, 31일부터 닷새 총파업 예고"
    };

    System.out.println("\n===== 제목 간 해밍거리 =====");
    for (int i = 0; i < titles.length; i++) {
      for (int j = i + 1; j < titles.length; j++) {
        int d = SimHash.hammingDistance(SimHash.of(titles[i]), SimHash.of(titles[j]));
        if (d <= 15) {   // 가까운 것만 출력
          System.out.printf("거리 %2d : [%d] %s%n          [%d] %s%n",
              d, i, titles[i], j, titles[j]);
        }
      }
    }
    System.out.println("===========================\n");
  }
}
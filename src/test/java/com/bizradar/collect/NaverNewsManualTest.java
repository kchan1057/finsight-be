package com.bizradar.collect;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NaverNewsManualTest {

  @Autowired
  NaverNewsSource naverNewsSource;

  @Test
  void 광주은행_뉴스_뽑아보기() {
    var articles = naverNewsSource.fetch("광주은행", 5);

    System.out.println("\n===== 가져온 기사 수: " + articles.size() + " =====");
    for (var a : articles) {
      System.out.println("\n제목    : " + a.title());
      System.out.println("발행    : " + a.publishedAt());
      System.out.println("원문링크: " + a.originalLink());
    }
    System.out.println("\n=====================================\n");
  }
}

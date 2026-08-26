package com.bizradar.collect;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class CollectFilterTest {

  @Autowired NaverNewsSource naverNewsSource;

  @Test
  void 필터_전후_비교() {
    String company = "카카오뱅크";
    List<RawArticle> articles = naverNewsSource.fetch(company, 10);

    String key = company.toLowerCase().replaceAll("[^0-9a-z가-힣]", "");

    System.out.println("\n===== '" + company + "' 수집 " + articles.size() + "건 =====");
    int relevant = 0;
    for (RawArticle a : articles) {
      String t = a.title() == null ? "" : a.title().toLowerCase().replaceAll("[^0-9a-z가-힣]", "");
      String d = a.description() == null ? "" : a.description().toLowerCase().replaceAll("[^0-9a-z가-힣]", "");
      boolean ok = t.contains(key);
      if (ok) relevant++;
      System.out.println((ok ? "  [O] " : "  [X] ") + a.title());
    }
    System.out.println("\n관련 " + relevant + "건 / 전체 " + articles.size() + "건\n");
  }
}

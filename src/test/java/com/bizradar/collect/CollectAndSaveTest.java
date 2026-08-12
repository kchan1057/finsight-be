package com.bizradar.collect;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional   // 진짜 네이버는 호출하되, DB 저장분은 끝나고 롤백
class CollectAndSaveTest {

  @Autowired NaverNewsSource naverNewsSource;   // Day 2: 수집
  @Autowired TrendItemWriter writer;            // D3: 저장
  @Autowired CompanyMapper companyMapper;
  @Autowired TrendItemMapper trendItemMapper;

  @Test
  void 수집해서_저장하고_다시_수집해도_안_늘어난다() {
    // 1) 기업 준비
    Long companyId = companyMapper.findIdByName("광주은행");
    if (companyId == null) {
      companyMapper.insert("광주은행");
      companyId = companyMapper.findIdByName("광주은행");
    }

    // 2) 진짜 네이버에서 수집
    List<RawArticle> articles = naverNewsSource.fetch("광주은행", 5);

    // 3) 첫 저장 → 새 기사들이 들어감
    int firstRun = writer.saveNew(companyId, "NAVER", articles);
    long afterFirst = trendItemMapper.countByCompany(companyId);

    // 4) 같은 걸 또 저장 → 이번엔 하나도 새로 안 들어가야 함
    int secondRun = writer.saveNew(companyId, "NAVER", articles);
    long afterSecond = trendItemMapper.countByCompany(companyId);

    System.out.println("\n첫 수집 저장: " + firstRun + "건");
    System.out.println("DB 누적    : " + afterFirst + "건");
    System.out.println("재수집 저장: " + secondRun + "건 (0이어야 정상)");
    System.out.println("DB 누적    : " + afterSecond + "건 (안 늘어야 정상)\n");

    assertThat(secondRun).isZero();               // 재수집은 0건
    assertThat(afterSecond).isEqualTo(afterFirst); // 총량 안 변함
  }
}

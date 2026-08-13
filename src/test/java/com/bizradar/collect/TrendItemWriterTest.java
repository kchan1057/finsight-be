package com.bizradar.collect;

import static org.assertj.core.api.Assertions.assertThat;

import com.bizradar.company.CompanyMapper;
import com.bizradar.trend.TrendItemMapper;
import com.bizradar.trend.TrendItemWriter;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class TrendItemWriterTest {

  @Autowired
  TrendItemWriter writer;
  @Autowired
  CompanyMapper companyMapper;
  @Autowired
  TrendItemMapper trendItemMapper;

  @Test
  void 추적꼬리만_다른_같은_기사는_한_번만_저장된다() {
    Long companyId = companyMapper.findIdByName("테스트은행");
    if (companyId == null) {
      companyMapper.insert("테스트은행");
      companyId = companyMapper.findIdByName("테스트은행");
    }

    var when = OffsetDateTime.parse("2026-08-12T11:04+09:00");
    var a1    = new RawArticle("제목A", "https://x.com/news/1?sc=Naver", "https://x.com/news/1", "요약", when);
    var a1dup = new RawArticle("제목A 재수집", "https://x.com/news/1",     "https://x.com/news/1", "요약", when);
    var a2    = new RawArticle("제목B", "https://x.com/news/2",           "https://x.com/news/2", "요약", when);

    int first  = writer.saveNew(companyId, "NAVER", List.of(a1));         // 1건 저장
    int second = writer.saveNew(companyId, "NAVER", List.of(a1dup, a2));  // a1dup 무시 + a2 저장 = 1

    assertThat(first).isEqualTo(1);
    assertThat(second).isEqualTo(1);
    assertThat(trendItemMapper.countByCompany(companyId)).isEqualTo(2);
  }
}

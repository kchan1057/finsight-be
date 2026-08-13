package com.bizradar.collect;

import com.bizradar.company.Company;
import com.bizradar.company.CompanyMapper;
import com.bizradar.trend.CollectLog;
import com.bizradar.trend.CollectLogMapper;
import com.bizradar.trend.TrendItemWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectService {

  private final CompanyMapper companyMapper;
  private final NaverNewsSource naverNewsSource;
  private final TrendItemWriter trendItemWriter;
  private final CollectLogMapper collectLogMapper;

  /** 등록된 모든 기업의 뉴스를 수집·저장하고, 기업별로 로그를 남긴다. */
  public void collectAll() {
    List<Company> companies = companyMapper.findAll();
    for (Company company : companies) {
      collectOne(company);
    }
  }

  private void collectOne(Company company) {
    try {
      List<RawArticle> articles = naverNewsSource.fetch(company.name(), 10);
      int newCount = trendItemWriter.saveNew(company.id(), "NAVER", articles);

      collectLogMapper.insert(new CollectLog(
          company.id(), "NAVER", "SUCCESS",
          articles.size(), newCount, null));

    } catch (Exception e) {
      // 한 기업이 실패해도 다음 기업은 계속 수집 (전체가 멈추지 않게)
      collectLogMapper.insert(new CollectLog(
          company.id(), "NAVER", "FAILED",
          0, 0, e.getMessage()));
    }
  }
}

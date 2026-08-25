package com.bizradar.trend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SummaryServiceTest {

  @Autowired SummaryService summaryService;
  @Autowired TrendItemMapper trendItemMapper;

  @Test
  void PENDING_기사들이_요약된다() {
    int before = trendItemMapper.findPending(100).size();
    int done = summaryService.summarizePending();
    int after = trendItemMapper.findPending(100).size();

    System.out.println("\n요약 전 PENDING: " + before + "건");
    System.out.println("이번에 요약 성공: " + done + "건");
    System.out.println("남은 PENDING   : " + after + "건\n");
  }
}

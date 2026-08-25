package com.bizradar.ai;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SummaryClientTest {

  @Autowired SummaryClient summaryClient;

  @Test
  void 실제로_요약이_나온다() {
    String prompt = SummaryPrompt.of(
        "광주은행",
        "광주은행, 전남 위기 소상공인 재도약에 힘 보탰다",
        "광주은행이 전남신용보증재단과 공동으로 추진하는 'S.O.S 프로젝트'가 지역 소상공인의 경영 회복과 매출 증대에 실질적인 성과를 내고 있다."
    );

    String summary = summaryClient.summarize(prompt);

    System.out.println("\n===== 요약 결과 =====");
    System.out.println(summary);
    System.out.println("길이: " + summary.length() + "자");
    System.out.println("====================\n");
  }
}

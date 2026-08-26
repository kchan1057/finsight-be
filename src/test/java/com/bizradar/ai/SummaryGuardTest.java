package com.bizradar.ai;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class SummaryGuardTest {

  private static final String TITLE = "카카오뱅크, 모바일 신분증 70만장 발급";

  @Test
  void 정상_요약은_통과한다() {
    String good = "카카오뱅크가 지난해 선보인 모바일 신분증 발급 건수가 70만 건을 넘어섰다.";
    var r = SummaryGuard.validate(good, TITLE);
    System.out.println("정상 요약 → " + (r.passed() ? "통과" : "거부: " + r.reason()));
    assertThat(r.passed()).isTrue();
  }

  @Test
  void 무의미_응답은_거부한다() {
    String bad = "제공된 뉴스에는 카카오뱅크 관련 정보가 없어 요약할 수 없습니다.";
    var r = SummaryGuard.validate(bad, TITLE);
    System.out.println("무의미 응답 → " + (r.passed() ? "통과" : "거부: " + r.reason()));
    assertThat(r.passed()).isFalse();
  }

  @Test
  void 너무_짧으면_거부한다() {
    var r = SummaryGuard.validate("없음", TITLE);
    System.out.println("짧은 응답 → " + (r.passed() ? "통과" : "거부: " + r.reason()));
    assertThat(r.passed()).isFalse();
  }

  @Test
  void 원문을_거의_그대로_베끼면_거부한다() {
    String copy = "카카오뱅크, 모바일 신분증 70만장 발급했다고 밝혔다";
    var r = SummaryGuard.validate(copy, TITLE);
    System.out.println("원문 복제 → " + (r.passed() ? "통과" : "거부: " + r.reason()));
    assertThat(r.passed()).isFalse();
  }
}
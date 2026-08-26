package com.bizradar.ai;

import com.bizradar.common.TitleSimilarity;

/** AI 요약 결과를 신뢰하지 않고 규칙으로 검증하는 계층. */
public final class SummaryGuard {

  private static final int MIN_LENGTH = 15;              // 이보다 짧으면 요약이 아님
  private static final int MAX_LENGTH = 300;             // 규칙(120자)을 크게 벗어나면 이상
  private static final double COPY_THRESHOLD = 0.60;     // 원문과 이 이상 비슷하면 복제로 간주

  /** AI가 "못 하겠다"고 답할 때 나오는 전형적 문구 */
  private static final String[] REFUSAL_PATTERNS = {
      "관련 정보가 없", "관련 내용이 없", "포함되어 있지 않", "포함되지 않",
      "요약할 수 없", "요약이 불가", "제공된 뉴스에는", "제시된 뉴스에는",
      "내용이 없어", "확인할 수 없", "죄송"
  };

  private SummaryGuard() {}

  /** 검증 결과 — 통과 여부와 사유 */
  public record Result(boolean passed, String reason) {
    static Result ok()               { return new Result(true, null); }
    static Result reject(String why) { return new Result(false, why); }
  }

  public static Result validate(String summary, String originalTitle) {
    if (summary == null || summary.isBlank()) {
      return Result.reject("빈 응답");
    }
    String s = summary.trim();

    // ① 길이 검사
    if (s.length() < MIN_LENGTH) {
      return Result.reject("너무 짧음(" + s.length() + "자)");
    }
    if (s.length() > MAX_LENGTH) {
      return Result.reject("너무 김(" + s.length() + "자)");
    }

    // ② 무의미(거부) 응답 패턴
    for (String pattern : REFUSAL_PATTERNS) {
      if (s.contains(pattern)) {
        return Result.reject("무의미 응답: " + pattern);
      }
    }

    // ③ 원문 복제 검사 (저작권) — 중복 판정용 자카드를 재사용
    double similarity = TitleSimilarity.similarity(s, originalTitle);
    if (similarity >= COPY_THRESHOLD) {
      return Result.reject(String.format("원문 복제 의심(유사도 %.2f)", similarity));
    }

    return Result.ok();
  }
}

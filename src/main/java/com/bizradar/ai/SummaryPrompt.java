package com.bizradar.ai;

public class SummaryPrompt {

  private SummaryPrompt() {}

  public static String of(String companyName, String title, String description) {
    return """
                당신은 기업 동향을 정리하는 애널리스트입니다.
                아래 뉴스를 읽고 '%s'의 동향 관점에서 요약하세요.

                [작성 규칙]
                - 원문의 문장을 그대로 복제하지 말고, 핵심 사실만 새로운 문장으로 다시 쓸 것
                - 2문장 이내, 120자 이내로 작성할 것
                - 원문에 없는 회사명·수치·사실을 추가하지 말 것
                - 추측·의견 없이 사실만 기술할 것
                - 요약문만 출력하고 다른 말은 덧붙이지 말 것

                [뉴스]
                제목: %s
                내용: %s
                """.formatted(companyName, title, description == null ? "" : description);
  }
}

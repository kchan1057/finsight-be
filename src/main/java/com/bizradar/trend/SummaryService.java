package com.bizradar.trend;

import com.bizradar.ai.SummaryClient;
import com.bizradar.ai.SummaryGuard;
import com.bizradar.ai.SummaryPrompt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SummaryService {

  private static final int BATCH_SIZE = 10;   // 한 번에 요약할 건수

  private final TrendItemMapper trendItemMapper;
  private final SummaryClient summaryClient;

  public SummaryService(TrendItemMapper trendItemMapper, SummaryClient summaryClient) {
    this.trendItemMapper = trendItemMapper;
    this.summaryClient = summaryClient;
  }

  /** PENDING 기사들을 요약해 상태를 갱신한다. 반환: 성공 건수 */
  public int summarizePending() {
    List<PendingItem> pending = trendItemMapper.findPending(BATCH_SIZE);

    int done = 0;
    for (PendingItem item : pending) {
      try {
        String prompt = SummaryPrompt.of(item.companyName(), item.title(), null);
        String summary = summaryClient.summarize(prompt);

        // 가드레일: AI 결과를 그대로 믿지 않고 검증
        SummaryGuard.Result check = SummaryGuard.validate(summary, item.title());
        if (!check.passed()) {
          trendItemMapper.markSkipped(item.id(), "[검증실패] " + check.reason());
          continue;                       // 저장하지 않음
        }

        trendItemMapper.updateSummary(item.id(), summary);
        done++;

      } catch (Exception e) {
        String reason = "[호출실패] " + e.getClass().getSimpleName() + ": "
            + (e.getMessage() == null ? "" : e.getMessage());
        trendItemMapper.markFailed(item.id(), reason.length() > 900 ? reason.substring(0, 900) : reason);
      }
    }
    return done;
  }
}

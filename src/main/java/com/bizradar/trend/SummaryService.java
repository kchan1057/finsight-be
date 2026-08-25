package com.bizradar.trend;

import com.bizradar.ai.SummaryClient;
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

        trendItemMapper.updateSummary(item.id(), summary);
        done++;
      } catch (Exception e) {
        // 한 건 실패가 나머지를 막지 않게 (D4에서 배운 격리)
        trendItemMapper.markFailed(item.id());
      }
    }
    return done;
  }
}

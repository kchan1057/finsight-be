package com.bizradar.trend;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SummaryScheduler {

  private final SummaryService summaryService;

  @Scheduled(initialDelayString = "PT1M", fixedDelayString = "PT5M")
  public void run() {
    summaryService.summarizePending();
  }
}

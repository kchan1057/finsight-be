package com.bizradar.collect;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CollectScheduler {

  private final CollectService collectService;

  @Scheduled(initialDelayString = "PT30S", fixedDelayString = "PT10M")
  public void run() {
    collectService.collectAll();
  }
}

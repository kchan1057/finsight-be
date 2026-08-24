package com.bizradar.common;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class SimHashTest {

  @Test
  void 거의_같은_제목은_해밍거리가_작다() {
    long a = SimHash.of("광주은행, 소상공인 지원 확대");
    long b = SimHash.of("[속보] 광주은행, 소상공인 지원 확대");

    int distance = SimHash.hammingDistance(a, b);
    System.out.println("거의 같은 제목 해밍거리 = " + distance);

    assertThat(distance).isLessThanOrEqualTo(3);   // 임계값 이하 = 같은 기사로 판정
  }

  @Test
  void 완전히_다른_제목은_해밍거리가_크다() {
    long a = SimHash.of("광주은행, 소상공인 지원 확대");
    long c = SimHash.of("토스뱅크, 해외 송금 수수료 무료화");

    int distance = SimHash.hammingDistance(a, c);
    System.out.println("다른 제목 해밍거리 = " + distance);

    assertThat(distance).isGreaterThan(3);   // 임계값 초과 = 다른 기사
  }

  @Test
  void 같은_제목은_지문도_같다() {
    long a = SimHash.of("광주은행 실적 발표");
    long b = SimHash.of("광주은행 실적 발표");
    assertThat(SimHash.hammingDistance(a, b)).isZero();   // 완전 동일 → 거리 0
  }
}
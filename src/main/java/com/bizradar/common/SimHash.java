package com.bizradar.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Set;

/** 제목 → 64비트 SimHash 지문. 2-gram(글자 2개씩)으로 토큰화해 한국어 조사 변화에 강함. */
public final class SimHash {

  private SimHash() {}

  public static long of(String text) {
    // 1) 전처리: 소문자 + 특수문자/공백 제거
    String cleaned = text.toLowerCase().replaceAll("[^0-9a-z가-힣]", "");
    if (cleaned.length() < 2) return 0L;

    // 2) 2-gram 토큰: "인공지능" → [인공, 공지, 지능]
    Set<String> tokens = new HashSet<>();
    for (int i = 0; i < cleaned.length() - 1; i++) {
      tokens.add(cleaned.substring(i, i + 2));
    }

    // 3) 64비트 투표
    int[] vote = new int[64];
    for (String token : tokens) {
      long h = hash64(token);
      for (int i = 0; i < 64; i++) {
        if (((h >>> i) & 1L) == 1L) vote[i]++;
        else                        vote[i]--;
      }
    }

    // 4) 최종 지문
    long simhash = 0L;
    for (int i = 0; i < 64; i++) {
      if (vote[i] > 0) simhash |= (1L << i);
    }
    return simhash;
  }

  public static int hammingDistance(long a, long b) {
    return Long.bitCount(a ^ b);
  }

  private static long hash64(String token) {
    try {
      byte[] d = MessageDigest.getInstance("SHA-256")
                              .digest(token.getBytes(StandardCharsets.UTF_8));
      long h = 0L;
      for (int i = 0; i < 8; i++) h = (h << 8) | (d[i] & 0xFF);
      return h;
    } catch (Exception e) {
      throw new IllegalStateException("해시 실패", e);
    }
  }
}

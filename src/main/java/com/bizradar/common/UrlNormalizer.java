package com.bizradar.common;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/** URL을 정규 형태로 통일하고 SHA-256 해시(중복 판정 지문)를 만든다. */
public final class UrlNormalizer {

  // 기사 식별과 무관한 추적 파라미터들 — 이것만 제거하고 나머지는 유지
  private static final Set<String> TRACKING_PARAMS = Set.of(
      "sc", "ref", "fbclid", "gclid", "igshid",
      "utm_source", "utm_medium", "utm_campaign", "utm_term", "utm_content"
  );

  private UrlNormalizer() {}

  /** 스킴·호스트 소문자화, 프래그먼트·추적 파라미터 제거, 남은 파라미터 정렬. */
  public static String normalize(String rawUrl) {
    try {
      URI uri = URI.create(rawUrl.trim());
      String scheme = lower(uri.getScheme());
      String host = lower(uri.getHost());
      int port = uri.getPort();
      String path = stripTrailingSlash(uri.getPath());
      String query = cleanQuery(uri.getRawQuery());

      StringBuilder sb = new StringBuilder();
      sb.append(scheme).append("://").append(host);
      if (port != -1 && !isDefaultPort(scheme, port)) sb.append(":").append(port);
      sb.append(path);
      if (!query.isEmpty()) sb.append("?").append(query);
      return sb.toString();
    } catch (Exception e) {
      return rawUrl.trim();   // 정규화 실패해도 최소한 저장은 되게 원본 반환
    }
  }

  /** 정규화한 URL의 SHA-256 해시(64자 hex). 이게 중복 판정 키. */
  public static String urlHash(String rawUrl) {
    return sha256Hex(normalize(rawUrl));
  }

  private static String cleanQuery(String rawQuery) {
    if (rawQuery == null || rawQuery.isBlank()) return "";
    return Arrays.stream(rawQuery.split("&"))
                 .map(p -> p.split("=", 2))
                 .filter(kv -> !TRACKING_PARAMS.contains(kv[0].toLowerCase(Locale.ROOT)))
                 .sorted(Comparator.comparing((String[] kv) -> kv[0]))
                 .map(kv -> kv.length == 2 ? kv[0] + "=" + kv[1] : kv[0])
                 .collect(Collectors.joining("&"));
  }

  private static String sha256Hex(String s) {
    try {
      byte[] digest = MessageDigest.getInstance("SHA-256")
                                   .digest(s.getBytes(StandardCharsets.UTF_8));
      StringBuilder hex = new StringBuilder(64);
      for (byte b : digest) hex.append(String.format("%02x", b));
      return hex.toString();
    } catch (Exception e) {
      throw new IllegalStateException("SHA-256 계산 실패", e);
    }
  }

  private static String lower(String s) { return s == null ? "" : s.toLowerCase(Locale.ROOT); }

  private static String stripTrailingSlash(String path) {
    if (path == null || path.isEmpty()) return "";
    return (path.length() > 1 && path.endsWith("/")) ? path.substring(0, path.length() - 1) : path;
  }

  private static boolean isDefaultPort(String scheme, int port) {
    return ("http".equals(scheme) && port == 80) || ("https".equals(scheme) && port == 443);
  }
}

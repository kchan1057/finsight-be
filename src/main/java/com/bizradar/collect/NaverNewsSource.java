package com.bizradar.collect;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

@Component
public class NaverNewsSource implements NewsSource{

  private final RestClient restClient;
  private final ObjectMapper objectMapper;

  private static final DateTimeFormatter RFC_1123 = DateTimeFormatter.RFC_1123_DATE_TIME.withLocale(
      Locale.ENGLISH);
  private static final ZoneId KST = ZoneId.of("Asia/Seoul");

  public NaverNewsSource(
      @Value("${naver.news.base-url}") String baseUrl,
      @Value("${naver.news.client-id}") String clientId,
      @Value("${naver.news.client-secret}") String clientSecret,
      ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;

    var httpClient = HttpClient.newBuilder()
                              .connectTimeout(Duration.ofSeconds(3))
                              .build();
    var requestFactory = new JdkClientHttpRequestFactory(httpClient);
    requestFactory.setReadTimeout(Duration.ofSeconds(5));

    this.restClient = RestClient.builder()
                                .requestFactory(requestFactory)
                                .baseUrl(baseUrl)
                                .defaultHeader("X-NCP-APIGW-API-KEY-ID", clientId)
                                .defaultHeader("X-NCP-APIGW-API-KEY", clientSecret)
                                .build();
  }

  @Override
  public List<RawArticle> fetch(String query, int display) {
    String json = restClient.get()
                            .uri(u -> u.queryParam("query", query)
                                       .queryParam("display", display)
                                       .queryParam("sort", "sim")
                                       .queryParam("format", "json")
                                       .build())
                            .retrieve()
                            .body(String.class);        // Content-Type 안 따지고 통짜로 받음

    NaverResponse res = parse(json);
    if (res == null || res.items() == null) return List.of();

    return res.items().stream().map(this::toRawArticle).toList();
  }

  private NaverResponse parse(String json) {
    try {
      return objectMapper.readValue(json, NaverResponse.class);
    } catch (Exception e) {
      throw new IllegalStateException("네이버 응답 파싱 실패: " + json, e);
    }
  }

  @Override
  public String sourceName() {
    return "NAVER";
  }

  private RawArticle toRawArticle(NaverItem item) {
    String originalLink = item.originallink();
    String naverLink = item.link();
    return new RawArticle(
        clean(item.title()),
        (originalLink == null || originalLink.isBlank()) ? naverLink : originalLink,
        naverLink,
        clean(item.description()),
        parseDate(item.pubDate())
    );
  }

  /** <b> 등 HTML 태그 제거 + &amp; 같은 엔티티 최소 복원. */
  private String clean(String raw) {
    if (raw == null) return "";
    return raw.replaceAll("<[^>]+>", "")   // 태그 제거
              .replace("&lt;", "<").replace("&gt;", ">")
              .replace("&amp;", "&").replace("&quot;", "\"")
              .replace("&#39;", "'").replace("&apos;", "'")
              .trim();
  }

  /** "Thu, 11 Jun 2026 18:34:00 +0900" → KST OffsetDateTime. */
  private OffsetDateTime parseDate(String pubDate) {
    return OffsetDateTime.parse(pubDate, RFC_1123)
                         .atZoneSameInstant(KST)
                         .toOffsetDateTime();
  }

  // 네이버 응답 매핑용 내부 record
  private record NaverResponse(List<NaverItem> items) {}
  private record NaverItem(String title, String originallink, String link, String description, String pubDate) {}
}

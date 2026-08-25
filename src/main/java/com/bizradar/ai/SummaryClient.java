package com.bizradar.ai;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

@Component
public class SummaryClient {

  private final RestClient restClient;
  private final ObjectMapper objectMapper;
  private final String model;

  public SummaryClient(
      @Value("${gemini.base-url}") String baseUrl,
      @Value("${gemini.api-key}") String apiKey,
      @Value("${gemini.model}") String model,
      ObjectMapper objectMapper) {
    this.model = model;
    this.objectMapper = objectMapper;

    var httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build();

    var requestFactory = new JdkClientHttpRequestFactory(httpClient);
    requestFactory.setReadTimeout(Duration.ofSeconds(30));

    this.restClient = RestClient.builder()
        .requestFactory(requestFactory)
        .baseUrl(baseUrl)
        .defaultHeader("x-goog-api-key", apiKey)
        .defaultHeader("Content-Type", "application/json")
        .build();
  }

  public String summarize(String prompt) {
    // 요청 형식: {"contents":[{"parts":[{"text": "..."}]}]}
    Map<String, Object> body = Map.of(
        "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))
    );

    String json = restClient.post()
                            .uri("/models/{model}:generateContent", model)
                            .body(body)
                            .retrieve()
                            .body(String.class);          // D2와 동일: 통짜로 받아 직접 파싱

    return extractText(json);
  }

  /** 응답에서 텍스트만 뽑는다: candidates[0].content.parts[0].text */
  private String extractText(String json) {
    try {
      var root = objectMapper.readTree(json);
      var text = root.path("candidates").path(0)
                     .path("content").path("parts").path(0).path("text");
      if (text.isMissingNode()) {
        throw new IllegalStateException("요약 응답에 text 없음: " + json);
      }
      return text.asString().trim();
    } catch (Exception e) {
      throw new IllegalStateException("Gemini 응답 파싱 실패: " + json, e);
    }
  }
}

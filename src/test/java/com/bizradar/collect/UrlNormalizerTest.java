package com.bizradar.collect;

import com.bizradar.common.UrlNormalizer;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class UrlNormalizerTest {

  @Test
  void 추적_파라미터는_제거한다() {
    String r = UrlNormalizer.normalize("https://www.dailian.co.kr/news/view/1677213/?sc=Naver");
    assertThat(r).isEqualTo("https://www.dailian.co.kr/news/view/1677213");
  }

  @Test
  void 식별용_파라미터는_유지한다() {
    String r = UrlNormalizer.normalize("http://www.bizwnews.com/news/articleView.html?idxno=143089");
    assertThat(r).isEqualTo("http://www.bizwnews.com/news/articleView.html?idxno=143089");
  }

  @Test
  void 꼬리만_다른_같은_기사는_같은_해시() {
    String h1 = UrlNormalizer.urlHash("https://www.dailian.co.kr/news/view/1677213/?sc=Naver");
    String h2 = UrlNormalizer.urlHash("https://www.dailian.co.kr/news/view/1677213");
    String h3 = UrlNormalizer.urlHash("https://www.dailian.co.kr/news/view/9999999");

    assertThat(h1).isEqualTo(h2);     // 추적 꼬리 유무는 같은 기사로
    assertThat(h1).isNotEqualTo(h3);  // 다른 기사면 다른 해시
    assertThat(h1).hasSize(64);       // SHA-256 hex 길이
  }
}

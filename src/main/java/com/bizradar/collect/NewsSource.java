package com.bizradar.collect;

import java.util.List;

/** 뉴스 소스 추상화. 네이버든 다른 소스든 이 계약만 지키면 교체 가능. */
public interface NewsSource {
  List<RawArticle> fetch(String query, int display);
  String sourceName();
}

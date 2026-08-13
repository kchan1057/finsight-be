package com.bizradar.collect;

import com.bizradar.company.CompanyMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CollectServiceTest {

  @Autowired CollectService collectService;
  @Autowired
  CompanyMapper companyMapper;

  @Test
  void 전체_기업_자동수집이_돈다() {
    // 관심 기업 몇 개 등록 (이미 있으면 UNIQUE라 무시되게 findAll로 확인)
    for (String name : new String[]{"광주은행", "카카오뱅크", "토스뱅크"}) {
      if (companyMapper.findIdByName(name) == null) companyMapper.insert(name);
    }

    collectService.collectAll();   // 실제 네이버 수집 + 저장 + 로그

    System.out.println("\n등록 기업 수: " + companyMapper.findAll().size());
    System.out.println("collect_log 는 DB에서 확인 → SELECT * FROM collect_log ORDER BY id DESC;\n");
  }
}

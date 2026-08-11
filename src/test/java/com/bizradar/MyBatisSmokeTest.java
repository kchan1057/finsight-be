package com.bizradar;

import com.bizradar.common.health.DbHealthMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class MyBatisSmokeTest {

  @Container
  @ServiceConnection  // 컨테이너 DB를 datasource로 자동 연결 (application.yml 불필요)
  static MySQLContainer<?> mysql =
      new MySQLContainer<>(DockerImageName.parse("mysql:8.4"));

  @Autowired
  DbHealthMapper dbHealthMapper;

  @Test
  void 스택이_조립되고_마이그레이션이_적용된다() {
    assertThat(dbHealthMapper.selectOne()).isEqualTo(1);      // MyBatis 배선 OK
    assertThat(dbHealthMapper.countCompany()).isZero();       // Flyway 적용 + 테이블 존재 OK
  }
}

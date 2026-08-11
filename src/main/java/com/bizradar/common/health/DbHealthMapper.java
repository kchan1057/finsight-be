package com.bizradar.common.health;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DbHealthMapper {

  @Select("SELECT 1")
  int selectOne();

  @Select("SELECT COUNT(*) FROM company")
  long countCompany();
}

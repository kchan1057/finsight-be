package com.bizradar.collect;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CompanyMapper {

  @Insert("INSERT INTO company (name) VALUES (#{name})")
  int insert(@Param("name") String name);

  @Select("SELECT id FROM company WHERE name = #{name}")
  Long findIdByName(@Param("name") String name);
}

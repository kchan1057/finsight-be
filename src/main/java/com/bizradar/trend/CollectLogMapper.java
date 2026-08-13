package com.bizradar.trend;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CollectLogMapper {

  @Insert("""
          INSERT INTO collect_log(company_id, source, status, fetched_count, new_count, message)
          VALUES (#{companyId}, #{source}, #{status}, #{fetchedCount}, #{newCount}, #{message})
          """)
  int insert(CollectLog log);
}

package com.bizradar.trend;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TrendItemMapper {

  @Insert("""
          INSERT IGNORE INTO trend_item
            (company_id, title, published_at, source, origin_url, url_hash)
          VALUES
            (#{companyId}, #{title}, #{publishedAt}, #{source}, #{originUrl}, #{urlHash})
          """)
  int insertIgnore(TrendItem item);

  @Select("SELECT COUNT(*) FROM trend_item WHERE company_id = #{companyId}")
  long countByCompany(long companyId);
}

package com.bizradar.trend;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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

  @Select("""
      SELECT simhash FROM trend_item
      WHERE company_id = #{companyId}
        AND simhash IS NOT NULL
        AND published_at >= DATE_SUB(NOW(), INTERVAL #{days} DAY)
      """)
  List<Long> findRecentSimhashes(
      @Param("companyId") long companyId,
      @Param("days") int days);

  @Select("""
          SELECT t.id, t.title, t.source, c.name AS companyName
          FROM trend_item t JOIN company c ON t.company_id = c.id
          WHERE t.summary_status = 'PENDING'
          ORDER BY t.published_at DESC
          LIMIT #{limit}
          """)
  List<PendingItem> findPending(@Param("limit") int limit);

  @Update("""
          UPDATE trend_item
          SET summary = #{summary}, summary_status = 'DONE'
          WHERE id = #{id}
          """)
  int updateSummary(@Param("id") long id, @Param("summary") String summary);

  @Update("UPDATE trend_item SET summary_status = 'FAILED' WHERE id = #{id}")
  int markFailed(@Param("id") long id);

}

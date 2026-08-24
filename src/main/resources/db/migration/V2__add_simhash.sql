-- 제목 SimHash 지문 (2계층 중복 판정용). 64비트 → BIGINT
ALTER TABLE trend_item ADD COLUMN simhash BIGINT NULL AFTER url_hash;
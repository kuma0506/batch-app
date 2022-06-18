-- DB切り替え
\c postgredb

-- テーブル作成
CREATE TABLE  batchapp.person (
  id SERIAL NOT NULL,
  first_name VARCHAR(20),
  last_name VARCHAR(20),
  PRIMARY KEY (id)
);

-- 権限追加
GRANT USAGE ON SEQUENCE batchapp.person_id_seq TO hoge;
GRANT ALL PRIVILEGES ON batchapp.person TO hoge;


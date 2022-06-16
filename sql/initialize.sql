-- DB作成
CREATE DATABASE postgredb; 

-- 作成したDBへ切り替え
\c postgredb

-- スキーマ作成
CREATE SCHEMA batchapp;

-- ロールの作成
CREATE ROLE hoge WITH LOGIN PASSWORD 'passw0rd';

-- 権限追加
GRANT ALL PRIVILEGES ON SCHEMA batchapp TO hoge;
CREATE DATABASE IF NOT EXISTS fraud;

DROP TABLE IF EXISTS fraud.fraud_payment;

create table fraud.fraud_payment (

  timestamp Date,
  id String,
  eventTime String,

  fraudType String,
  comment String
) ENGINE = MergeTree()
PARTITION BY toYYYYMM (timestamp)
ORDER BY (eventTime, id);
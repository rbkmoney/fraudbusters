CREATE DATABASE IF NOT EXISTS fraud;

DROP TABLE IF EXISTS fraud.events_unique;

create table fraud.events_unique (
  timestamp Date,
  shopId String,
  partyId String,
  ip String,
  email String,
  bin String,
  fingerprint String,
  resultStatus String,
  amount UInt64,
  eventTime UInt64
) ENGINE = MergeTree(timestamp, (shopId, partyId, ip, email, bin, fingerprint, resultStatus), 8192);
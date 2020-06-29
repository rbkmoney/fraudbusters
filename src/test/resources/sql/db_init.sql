CREATE DATABASE IF NOT EXISTS fraud;

DROP TABLE IF EXISTS fraud.events_unique;

create table fraud.events_unique (
  timestamp Date,
  eventTimeHour UInt64,
  eventTime UInt64,

  partyId String,
  shopId String,

  ip String,
  email String,
  bin String,
  fingerprint String,
  resultStatus String,
  amount UInt64,
  country String,
  checkedRule String,
  bankCountry String,
  currency String,
  invoiceId String,
  maskedPan String,
  bankName String,
  cardToken String,
  paymentId String,
  checkedTemplate String
) ENGINE = MergeTree()
PARTITION BY toYYYYMM (timestamp)
ORDER BY (eventTimeHour, partyId, shopId, bin, resultStatus, cardToken, email, ip, fingerprint)
TTL timestamp + INTERVAL 3 MONTH;
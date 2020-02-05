DROP TABLE IF EXISTS fraud.events_p_to_p;

create table fraud.events_p_to_p (
  timestamp Date,
  eventTime UInt64,
  eventTimeHour UInt64,

  identityId String,
  transferId String,

  ip String,
  email String,
  bin String,
  fingerprint String,

  amount UInt64,
  currency String,

  country String,
  bankCountry String,
  maskedPan String,
  bankName String,
  cardTokenFrom String,
  cardTokenTo String,

  resultStatus String,
  checkedRule String,
  checkedTemplate String
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(timestamp)
ORDER BY (eventTimeHour, identityId, cardTokenFrom, cardTokenTo, bin, fingerprint, currency);
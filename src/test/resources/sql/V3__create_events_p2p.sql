DROP TABLE IF EXISTS fraud.events_p_to_p;

create table fraud.events_p_to_p (
  timestamp Date,
  identityId String,
  ip String,
  email String,
  bin String,
  fingerprint String,
  resultStatus String,
  amount UInt64,
  eventTime UInt64,
  country String,
  checkedRule String,
  bankCountry String,
  currency String,
  invoiceId String,
  maskedPan String,
  bankName String,
  cardTokenFrom String,
  cardTokenTo String,
  paymentId String,
  checkedTemplate String
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(timestamp)
ORDER BY (eventTime, shopId, partyId, ip, email, bin, fingerprint, resultStatus, cardTokenFrom, cardTokenTo);
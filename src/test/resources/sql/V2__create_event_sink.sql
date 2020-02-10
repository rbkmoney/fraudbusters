DROP TABLE IF EXISTS fraud.events_sink_mg;

create table fraud.events_sink_mg (
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
  errorCode String,
  errorMessage String,
  amount UInt64,
  country String,
  bankCountry String,
  currency String,
  invoiceId String,
  maskedPan String,
  bankName String,
  cardToken String,
  paymentId String
) ENGINE = MergeTree()
PARTITION BY toYYYYMM (timestamp)
ORDER BY (eventTime, partyId, shopId, bin, resultStatus, cardToken, email, ip, fingerprint);
DROP TABLE IF EXISTS fraud.events_sink_mg;

create table fraud.events_sink_mg (
  timestamp Date,
  shopId String,
  partyId String,
  ip String,
  email String,
  bin String,
  fingerprint String,
  resultStatus String,
  errorCode String,
  errorMessage String,
  amount UInt64,
  eventTime UInt64,
  country String,
  bankCountry String,
  currency String,
  invoiceId String,
  maskedPan String,
  bankName String,
  cardToken String,
  paymentId String
) ENGINE = MergeTree(timestamp, (shopId, partyId, ip, email, bin, fingerprint, resultStatus, cardToken), 8192);

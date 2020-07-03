CREATE DATABASE IF NOT EXISTS fraud;

DROP TABLE IF EXISTS fraud.fraud_payment;

create table fraud.fraud_payment (

  timestamp Date,
  id String,
  eventTime String,

  partyId String,
  shopId String,

  amount UInt64,
  currency String,

  payerType String,
  paymentToolType String,
  cardToken String,
  paymentSystem String,
  maskedPan String,
  issuerCountry String,
  email String,
  ip String,
  fingerprint String,
  status String,
  rrn String,

  providerId UInt32,
  terminalId UInt32,

  tempalateId String,
  description String

) ENGINE = MergeTree()
PARTITION BY toYYYYMM (timestamp)
ORDER BY (partyId, shopId, paymentToolType, status, currency, providerId, fingerprint, cardToken, id);
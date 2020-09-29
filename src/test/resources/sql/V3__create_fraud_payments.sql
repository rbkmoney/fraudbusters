CREATE DATABASE IF NOT EXISTS fraud;

DROP TABLE IF EXISTS fraud.fraud_payment;

create table fraud.fraud_payment (

timestamp Date,
  id String,
  eventTime UInt64,
  eventTimeHour UInt64,

  fraudType String,
  comment String,

    email                 String,
    ip                    String,
    fingerprint           String,

    bin                   String,
    maskedPan             String,
    cardToken             String,
    paymentSystem         String,
    paymentTool           String,

    terminal              String,
    providerId            String,
    bankCountry           String,

    partyId               String,
    shopId                String,

    amount                UInt64,
    currency              String,

    status                Enum8('pending' = 1, 'processed' = 2, 'captured' = 3, 'cancelled' = 4, 'failed' = 5),
    errorReason           String,
    errorCode             String,
    paymentCountry        String
) ENGINE = MergeTree()
PARTITION BY toYYYYMM (timestamp)
ORDER BY (eventTimeHour, partyId, shopId, paymentTool, status, currency, providerId, fingerprint, cardToken, eventTime, id);

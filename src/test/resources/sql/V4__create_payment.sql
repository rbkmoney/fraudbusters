DROP TABLE IF EXISTS fraud.refund;

create table fraud.refund
(
    timestamp             Date,
    eventTime             UInt64,
    eventTimeHour         UInt64,

    id                    String,

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

    status                Enum8('pending' = 1, 'succeeded' = 2, 'failed' = 3),
    errorReason           String,
    errorCode             String,
    paymentId             String
) ENGINE = ReplacingMergeTree()
PARTITION BY toYYYYMM (timestamp)
ORDER BY (eventTimeHour, partyId, shopId, status, currency, providerId, fingerprint, cardToken, id, paymentId);

DROP TABLE IF EXISTS fraud.payment;

create table fraud.payment
(
    timestamp             Date,
    eventTime             UInt64,
    eventTimeHour         UInt64,

    id                    String,

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
) ENGINE = ReplacingMergeTree()
PARTITION BY toYYYYMM (timestamp)
ORDER BY (eventTimeHour, partyId, shopId, paymentTool, status, currency, providerId, fingerprint, cardToken, id);

DROP TABLE IF EXISTS fraud.chargeback;

create table fraud.chargeback
(
    timestamp             Date,
    eventTime             UInt64,
    eventTimeHour         UInt64,

    id                    String,

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

    status                Enum8('accepted' = 1, 'rejected' = 2, 'cancelled' = 3),

    category              Enum8('fraud' = 1, 'dispute' = 2, 'authorisation' = 3, 'processing_error' = 4),
    chargebackCode        String,
    paymentId             String
) ENGINE = ReplacingMergeTree()
PARTITION BY toYYYYMM (timestamp)
ORDER BY (eventTimeHour, partyId, shopId, category, status, currency, providerId, fingerprint, cardToken, id, paymentId);
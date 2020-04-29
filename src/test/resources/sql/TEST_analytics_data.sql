CREATE DATABASE IF NOT EXISTS analytic;

DROP TABLE IF EXISTS analytic.events_sink;

create table analytic.events_sink
(
    timestamp             Date,
    eventTime             UInt64,
    eventTimeHour         UInt64,

    partyId               String,
    shopId                String,

    email                 String,
    providerName          String,

    amount                UInt64,
    guaranteeDeposit      UInt64,
    systemFee             UInt64,
    providerFee           UInt64,
    externalFee           UInt64,
    currency              String,

    status                Enum8('pending' = 1, 'processed' = 2, 'captured' = 3, 'cancelled' = 4, 'failed' = 5),
    errorReason           String,
    errorCode             String,

    invoiceId             String,
    paymentId             String,
    sequenceId            UInt64,

    ip                    String,
    bin                   String,
    maskedPan             String,
    paymentTool           String,
    fingerprint           String,
    cardToken             String,
    paymentSystem         String,
    digitalWalletProvider String,
    digitalWalletToken    String,
    cryptoCurrency        String,
    mobileOperator        String,

    paymentCountry        String,
    bankCountry           String,

    paymentTime           UInt64,
    providerId            String,
    terminal              String
) ENGINE = ReplacingMergeTree()
PARTITION BY toYYYYMM (timestamp)
ORDER BY (eventTimeHour, partyId, shopId, paymentTool, status, currency, providerName, fingerprint, cardToken, invoiceId, paymentId, sequenceId);

DROP TABLE IF EXISTS analytic.events_sink_refund;

create table analytic.events_sink_refund
(
    timestamp             Date,
    eventTime             UInt64,
    eventTimeHour         UInt64,

    partyId               String,
    shopId                String,

    email                 String,
    providerName          String,

    amount                UInt64,
    guaranteeDeposit      UInt64,
    systemFee             UInt64,
    providerFee           UInt64,
    externalFee           UInt64,
    currency              String,

    reason                String,

    status                Enum8('pending' = 1, 'succeeded' = 2, 'failed' = 3),
    errorReason           String,
    errorCode             String,

    invoiceId             String,
    refundId              String,
    paymentId             String,
    sequenceId            UInt64,

    ip                    String,
    fingerprint           String,
    cardToken             String,
    paymentSystem         String,
    digitalWalletProvider String,
    digitalWalletToken    String,
    cryptoCurrency        String,
    mobileOperator        String,

    paymentCountry        String,
    bankCountry           String,

    paymentTime           UInt64,
    providerId            String,
    terminal              String
) ENGINE = ReplacingMergeTree()
PARTITION BY toYYYYMM (timestamp)
ORDER BY (eventTimeHour, partyId, shopId, status, currency, providerName, fingerprint, cardToken, invoiceId, paymentId, refundId, sequenceId);

DROP TABLE IF EXISTS analytic.events_sink_adjustment;

create table analytic.events_sink_adjustment
(
    timestamp             Date,
    eventTime             UInt64,
    eventTimeHour         UInt64,

    partyId               String,
    shopId                String,

    email                 String,
    providerName          String,

    amount                UInt64,
    guaranteeDeposit      UInt64,
    systemFee             UInt64,
    providerFee           UInt64,
    externalFee           UInt64,

    oldAmount             UInt64,
    oldGuaranteeDeposit   UInt64,
    oldSystemFee          UInt64,
    oldProviderFee        UInt64,
    oldExternalFee        UInt64,

    currency              String,

    reason                String,

    status                Enum8('captured' = 1, 'cancelled' = 2),
    errorCode             String,
    errorReason           String,

    invoiceId             String,
    adjustmentId          String,
    paymentId             String,
    sequenceId            UInt64,

    ip                    String,
    fingerprint           String,
    cardToken             String,
    paymentSystem         String,
    digitalWalletProvider String,
    digitalWalletToken    String,
    cryptoCurrency        String,
    mobileOperator        String,

    paymentCountry        String,
    bankCountry           String,

    paymentTime           UInt64,
    providerId            String,
    terminal              String

) ENGINE = ReplacingMergeTree()
PARTITION BY toYYYYMM (timestamp)
ORDER BY (eventTimeHour, partyId, shopId, status, currency, providerName, fingerprint, cardToken, invoiceId, paymentId, adjustmentId, sequenceId);
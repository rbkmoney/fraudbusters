create table fraud.withdrawal
(
    timestamp             Date,
    eventTime             UInt64,
    eventTimeHour         UInt64,

    id                    String,

    amount                UInt64,
    currency              String,

    bin                   String,
    maskedPan             String,
    cardToken             String,
    paymentSystem         String,
    paymentTool           String,
    bankName              String,
    cardHolderName        String,
    issuerCountry         String,

    cryptoWalletId        String,
    cryptoWalletCurrency  String,

    terminal              String,
    providerId            String,
    bankCountry           String,

    identityId               String,
    accountId                String,
    walletId                String,

    status                Enum8('pending' = 1, 'succeeded' = 2, 'failed' = 3),
    errorReason           String,
    errorCode             String

) ENGINE = ReplacingMergeTree()
PARTITION BY toYYYYMM (timestamp)
ORDER BY (eventTimeHour, identityId, status, currency, paymentSystem, providerId, cardToken, id);

DROP TABLE IF EXISTS fraud.withdrawal;

package com.rbkmoney.fraudbusters.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DgraphSchemaConstants {

    public static final String SCHEMA = """ 
                payments: [uid] @count .
                emails: [uid] .
                fingerprints: [uid] .
                tokens: [uid] .
                lastActTime: datetime @index(day) .
                tokenId: string @index(exact) @upsert .
                maskedPan: string .
                paymentSystem: string .
                bin: uid .
                refunds: [uid] .
                chargebacks: [uid] .
                tokenizationMethod: string .
                paymentSystem: string .
                issuerCountry: string .
                bankName: string .
                cardholderName: string .
                category: string .
                
                type Token {
                    tokenId
                    maskedPan
                    tokenizationMethod
                    paymentSystem
                    issuerCountry
                    bankName
                    cardholderName
                    category
                    lastActTime
                    bin: Bin
                    payments: Payment
                    emails: Email
                    fingerprints: Fingerprint
                    refunds: Refund
                    chargebacks: Chargeback
                }
                
                paymentId: string @index(hash) @upsert .
                createdAt: dateTime @index(day) .
                amount: int .
                currency: string @index(hash) .
                status: string @index(hash) .
                cardToken: uid .
                fingerprint: uid .
                contactEmail: uid .
                partyId: string @index(hash) @upsert .
                shopId: string @index(hash) @upsert .
                paymentTool: string .
                terminal: string .
                providerId: string .
                bankCountry: string .
                payerType: string .
                tokenProvider: string .
                mobile: bool .
                recurrent: bool .
                errorReason: string .
                errorCode: string .
                checkedTemplate: string .
                checkedRule: string .
                resultStatus: string .
                checkedResultsJson: string .
                party: uid .
                shop: uid .
                shops: [uid] .
                country: uid .
                fraudPayment: uid .
                operationIp: uid .
                
                type Payment {
                    paymentId
                    createdAt
                    amount
                    currency
                    status
                    
                    paymentTool
                    terminal
                    providerId
                    bankCountry
                    payerType
                    tokenProvider
                    mobile
                    recurrent
                    errorReason
                    errorCode
                    checkedTemplate
                    checkedRule
                    resultStatus
                    checkedResultsJson
                    cardToken: Token
                    fingerprint: Fingerprint
                    contactEmail: Email
                    party: Party
                    shop: Shop
                    bin: Bin
                    country: Country
                    fraudPayment: FraudPayment
                    refunds: Refund
                    chargebacks: Chargeback
                    operationIp: IP
                }
                
                fingerprintData: string @index(hash) @upsert .
                
                type Fingerprint {
                    fingerprintData
                    lastActTime
                    emails: Email
                    tokens: Token
                    payments: Payment
                    refunds: Refund
                    chargebacks: Chargeback
                }
                
                userEmail: string @index(hash) @upsert .
                
                type Email {
                    userEmail
                    lastActTime
                    fingerprints: Fingerprint
                    tokens: Token
                    payments: Payment
                    refunds: Refund
                    chargebacks: Chargeback
                }
                
                type Party {
                    partyId
                    lastActTime
                    payments: Payment
                    tokens: Token
                    emails: Email
                    refunds: Refund
                    chargebacks: Chargeback
                    shops: Shop
                }
                
                type Shop {
                    shopId
                    lastActTime
                    payments: Payment
                    tokens: Token
                    emails: Email
                    refunds: Refund
                    chargebacks: Chargeback
                    party: Party
                }
                
                countryName: string @index(hash) @upsert .
                ips: [uid] .
                
                type Country {
                    countryName
                    lastActTime
                    payments: Payment
                    tokens: Token
                    emails: Email
                    ips: IP
                }
                
                ipAddress: string @index(hash) @upsert .
                countries: [uid] .
                
                type IP {
                    ipAddress
                    lastActTime
                    payments: Payment
                    tokens: Token
                    emails: Email
                    countries: Country
                    refunds: Refund
                    chargebacks: Chargeback
                }
                         
                cardBin: string @index(hash) @upsert .
                
                type Bin {
                    cardBin
                    lastActTime
                    payments: Payment
                    tokens: Token
                    emails: Email
                    refunds: Refund
                    chargebacks: Chargeback
                }
                
                fraudType: string .
                comment: string .
                sourcePayment: uid .
                
                type FraudPayment {
                    paymentId
                    createdAt
                    fraudType
                    comment
                    sourcePayment: Payment
                }
                
                refundId: string @index(hash) @upsert .
                
                type Refund {
                    refundId
                    paymentId
                    createdAt
                    amount
                    currency
                    status
                    payerType
                    errorCode
                    errorReason
                    bin: Bin
                    sourcePayment: Payment
                    cardToken: Token
                    fingerprint: Fingerprint
                    contactEmail: Email
                    party: Party
                    shop: Shop
                    operationIp: IP
                }
                
                chargebackId: string @index(hash) @upsert .
                category: string .
                code: string .
                
                type Chargeback {
                    chargebackId
                    paymentId
                    createdAt
                    amount
                    currency
                    status
                    category
                    code
                    payerType
                    bin: Bin
                    sourcePayment: Payment
                    cardToken: Token
                    fingerprint: Fingerprint
                    contactEmail: Email
                    party: Party
                    shop: Shop
                    operationIp: IP
                }
                
                withdrawalId: string @index(hash) @upsert .
                terminalId: string .
                accountId: string .
                accountIdentity: string .
                accountCurrency: string .
                destinationResource: string .
                digitalWalletId: string .
                digitalWalletDataProvider: string .
                cryptoWalletId: string .
                cryptoWalletCurrency: string .
                
                type Withdrawal {
                    withdrawalId
                    createdAt
                    amount
                    currency
                    status
                    providerId
                    terminalId
                    accountId
                    accountIdentity
                    accountCurrency
                    errorCode
                    errorReason
                    destinationResource
                    digitalWalletId
                    digitalWalletDataProvider
                    cryptoWalletId
                    cryptoWalletCurrency
                    country: Country
                    bin: Bin
                    cardToken: Token
                }
                
            """;
}

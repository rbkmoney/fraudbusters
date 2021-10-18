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
                
                type Token {
                    tokenId
                    maskedPan
                    bin
                    lastActTime
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
                partyId: string @index(hash) .
                shopId: string @index(hash) .
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
                partyShop: uid .
                country: uid .
                fraudPayment: uid .
                paymentIp: uid .
                
                type Payment {
                    paymentId
                    partyId
                    shopId
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
                    partyShop: PartyShop
                    bin: Bin
                    country: Country
                    fraudPayment: FraudPayment
                    refunds: Refund
                    chargebacks: Chargeback
                    paymentIp: IP
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
                
                type PartyShop {
                    partyId
                    shopId
                    payments: Payment
                    tokens: Token
                    emails: Email
                    refunds: Refund
                    chargebacks: Chargeback
                }
                
                countryName: string @index(hash) @upsert .
                ips: [uid] .
                
                type Country {
                    countryName
                    payments: Payment
                    tokens: Token
                    emails: Email
                    ips: IP
                }
                
                ipAddress: string @index(hash) @upsert .
                countries: [uid] .
                
                type IP {
                    ipAddress
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
                refundIp: uid .
                
                type Refund {
                    refundId
                    paymentId
                    partyId
                    shopId
                    createdAt
                    amount
                    currency
                    status
                    payerType
                    errorCode
                    errorReason
                    sourcePayment: Payment
                    cardToken: Token
                    fingerprint: Fingerprint
                    contactEmail: Email
                    partyShop: PartyShop
                    refundIp: IP
                }
                
                chargebackId: string @index(hash) @upsert .
                chargebackIp: uid .
                category: string .
                code: string .
                
                type Chargeback {
                    chargebackId
                    paymentId
                    partyId
                    shopId
                    createdAt
                    amount
                    currency
                    status
                    category
                    code
                    payerType
                    sourcePayment: Payment
                    cardToken: Token
                    fingerprint: Fingerprint
                    contactEmail: Email
                    partyShop: PartyShop
                    chargebackIp: IP
                }
                
            """;
}

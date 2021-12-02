package com.rbkmoney.fraudbusters.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DgraphSchemaConstants {

    private static final String TYPES = """
            type Payment {
                paymentId
                createdAt
                amount
                currency: Currency
                status
                paymentTool
                terminal
                providerId
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
                operationIp: Ip
            }
            
            type FraudPayment {
                paymentId
                createdAt
                fraudType
                comment
                sourcePayment: Payment
            }
            
            type Refund {
                refundId
                paymentId
                createdAt
                amount
                currency: Currency
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
                operationIp: Ip
            }
            
            type Chargeback {
                chargebackId
                paymentId
                createdAt
                amount
                currency: Currency
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
                operationIp: Ip
            }
            
            type Currency {
                currencyCode
                payments: Payment
                refunds: Refund
                chargebacks: Chargeback
                withdrawals: Withdrawal
            }
            
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
                payments: Payment
                refunds: Refund
                chargebacks: Chargeback
                emails: Email
                fingerprints: Fingerprint
                bin: Bin
            }
            
            type Bin {
                cardBin
                lastActTime
                payments: Payment
                refunds: Refund
                chargebacks: Chargeback
                emails: Email
                tokens: Token
            }
            
            type Email {
                userEmail
                lastActTime
                payments: Payment
                refunds: Refund
                chargebacks: Chargeback
                fingerprints: Fingerprint
                tokens: Token
            }
            
            type Fingerprint {
                fingerprintData
                lastActTime
                payments: Payment
                refunds: Refund
                chargebacks: Chargeback
                emails: Email
                tokens: Token
            }
            
            type Ip {
                ipAddress
                lastActTime
                payments: Payment
                refunds: Refund
                chargebacks: Chargeback
                tokens: Token
                emails: Email
                fingerprints: Fingerprint
                countries: Country
            }
            
            type Country {
                countryName
                lastActTime
                payments: Payment
                tokens: Token
                emails: Email
                fingerprints: Fingerprint
                ips: Ip
            }
            
            type Party {
                partyId
                shops: Shop
                lastActTime
                payments: Payment
                refunds: Refund
                chargebacks: Chargeback
                emails: Email
                fingerprints: Fingerprint
                tokens: Token
            }
            
            type Shop {
                party: Party
                shopId
                lastActTime
                payments: Payment
                refunds: Refund
                chargebacks: Chargeback
                tokens: Token
                emails: Email
                fingerprints: Fingerprint
            }
            
            type Withdrawal {
                withdrawalId
                createdAt
                amount
                currency: Currency
                status
                providerId
                terminalId
                accountId
                accountIdentity
                accountCurrency: Currency
                errorCode
                errorReason
                destinationResource
                digitalWalletId
                digitalWalletDataProvider
                cryptoWalletId
                cryptoWalletCurrency: Currency
                country: Country
                bin: Bin
                cardToken: Token
            }
            
            """;

    public static final String SCHEMA = DgraphSchemaFieldsConstants.SCHEMA_FIELDS + TYPES;

}

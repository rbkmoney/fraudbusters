package com.rbkmoney.fraudbusters.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DgraphSchemaFieldsConstants {

    public static final String SCHEMA_FIELDS = """
           
            accountCurrency: uid .
            accountId: string .
            accountIdentity: string .
            amount: int .
            bankName: string .
            bin: uid .
            cardBin: string @index(hash) @upsert .
            cardholderName: string .
            cardToken: uid .
            category: string .
            category: string .
            chargebackId: string @index(hash) @upsert .
            chargebacks: [uid] .
            checkedResultsJson: string .
            checkedRule: string .
            checkedTemplate: string .
            code: string .
            comment: string .
            contactEmail: uid .
            countries: [uid] .
            country: uid .
            countryName: string @index(hash) @upsert .
            createdAt: dateTime @index(day) .
            cryptoWalletCurrency: uid .
            cryptoWalletId: string .
            currency: uid .
            currencyCode: string @index(hash) @upsert .
            destinationResource: string .
            digitalWalletDataProvider: string .
            digitalWalletId: string .
            emails: [uid] .
            errorCode: string .
            errorReason: string .
            fingerprint: uid .
            fingerprintData: string @index(hash) @upsert .
            fingerprints: [uid] .
            fraudPayment: uid .
            fraudType: string .
            ipAddress: string @index(hash) @upsert .
            ips: [uid] .
            issuerCountry: string .
            lastActTime: datetime @index(day) .
            maskedPan: string .
            mobile: bool .
            operationIp: uid .
            party: uid .
            partyId: string @index(hash) @upsert .
            payerType: string .
            paymentId: string @index(hash) @upsert .
            payments: [uid] @count .
            paymentSystem: string .
            paymentSystem: string .
            paymentTool: string .
            providerId: string .
            recurrent: bool .
            refundId: string @index(hash) @upsert .
            refunds: [uid] .
            resultStatus: string .
            shop: uid .
            shopId: string @index(hash) @upsert .
            shops: [uid] .
            sourcePayment: uid .
            status: string @index(hash) .
            terminal: string .
            terminalId: string .
            tokenId: string @index(exact) @upsert .
            tokenizationMethod: string .
            tokenProvider: string .
            tokens: [uid] .
            userEmail: string @index(hash) @upsert .
            withdrawalId: string @index(hash) @upsert .
            withdrawals: [uid] .
            
            """;

}

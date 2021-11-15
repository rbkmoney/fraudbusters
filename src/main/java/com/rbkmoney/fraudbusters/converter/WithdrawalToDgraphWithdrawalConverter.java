package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.domain.BankCard;
import com.rbkmoney.damsel.fraudbusters.Error;
import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.domain.dgraph.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import static com.rbkmoney.fraudbusters.constant.ClickhouseUtilsValue.UNKNOWN;

@Slf4j
@Component
@RequiredArgsConstructor
public class WithdrawalToDgraphWithdrawalConverter implements Converter<Withdrawal, DgraphWithdrawal> {

    @Override
    public DgraphWithdrawal convert(Withdrawal withdrawal) {
        DgraphWithdrawal dgraphWithdrawal = new DgraphWithdrawal();
        dgraphWithdrawal.setWithdrawalId(withdrawal.getId());
        String withdrawalEventTime = withdrawal.getEventTime();
        dgraphWithdrawal.setCreatedAt(withdrawalEventTime);
        dgraphWithdrawal.setAmount(withdrawal.getCost().getAmount());
        dgraphWithdrawal.setCurrency(
                createDgraphCurrency(withdrawal.getCost().getCurrency().getSymbolicCode())
        );
        dgraphWithdrawal.setStatus(withdrawal.getStatus().name());

        Account account = withdrawal.getAccount();
        if (account != null) {
            dgraphWithdrawal.setAccountId(account.getId());
            dgraphWithdrawal.setAccountIdentity(account.getIdentity());
            dgraphWithdrawal.setAccountCurrency(
                    account.getCurrency() == null
                            ? null : createDgraphCurrency(account.getCurrency().getSymbolicCode()));
        }
        Error error = withdrawal.getError();
        if (error != null) {
            dgraphWithdrawal.setErrorCode(error.getErrorCode());
            dgraphWithdrawal.setErrorReason(error.getErrorReason());
        }

        ProviderInfo providerInfo = withdrawal.getProviderInfo();
        if (providerInfo != null) {
            dgraphWithdrawal.setProviderId(providerInfo.getProviderId());
            dgraphWithdrawal.setTerminalId(providerInfo.getTerminalId());
            dgraphWithdrawal.setCountry(
                    providerInfo.getCountry() == null ? null : toDgraphCountryObject(providerInfo.getCountry()));
        }

        Resource destinationResource = withdrawal.getDestinationResource();
        if (destinationResource != null) {
            dgraphWithdrawal.setDestinationResource(destinationResource.getSetField().getFieldName());
            if (destinationResource.isSetBankCard()) {
                final BankCard bankCard = destinationResource.getBankCard();
                dgraphWithdrawal.setCardToken(convertToken(withdrawalEventTime, bankCard));
                dgraphWithdrawal.setBin(bankCard.getBin() == null ? null : convertBin(bankCard.getBin()));
            } else if (destinationResource.isSetDigitalWallet()) {
                DigitalWallet digitalWallet = destinationResource.getDigitalWallet();
                dgraphWithdrawal.setDigitalWalletId(digitalWallet.getId());
                dgraphWithdrawal.setDigitalWalletDataProvider(digitalWallet.getDigitalDataProvider());
            } else if (destinationResource.isSetCryptoWallet()) {
                CryptoWallet cryptoWallet = destinationResource.getCryptoWallet();
                dgraphWithdrawal.setCryptoWalletId(cryptoWallet.getId());
                dgraphWithdrawal.setCryptoWalletCurrency(createDgraphCurrency(cryptoWallet.getCurrency()));
            } else {
                log.warn("Destination resource '{}' cannot be processed!",
                        destinationResource.getSetField().getFieldName());
            }
        }

        return dgraphWithdrawal;
    }

    private DgraphCurrency createDgraphCurrency(String currency) {
        DgraphCurrency dgraphCurrency = new DgraphCurrency();
        dgraphCurrency.setCurrencyCode(currency);
        return dgraphCurrency;
    }

    private DgraphToken convertToken(String withdrawalCreatedAt, BankCard bankCard) {
        DgraphToken dgraphToken = new DgraphToken();
        dgraphToken.setTokenId(bankCard.getToken() == null ? UNKNOWN : bankCard.getToken());
        dgraphToken.setMaskedPan(bankCard.getLastDigits() == null ? UNKNOWN : bankCard.getLastDigits());
        dgraphToken.setLastActTime(withdrawalCreatedAt);
        dgraphToken.setTokenizationMethod(
                bankCard.getTokenizationMethod() == null ? null : bankCard.getTokenizationMethod().name());
        dgraphToken.setPaymentSystem(bankCard.getPaymentSystem() == null ? null : bankCard.getPaymentSystem().getId());
        dgraphToken.setIssuerCountry(bankCard.getIssuerCountry() == null ? null : bankCard.getIssuerCountry().name());
        dgraphToken.setBankName(bankCard.getBankName());
        dgraphToken.setCardholderName(bankCard.getCardholderName());
        dgraphToken.setCategory(bankCard.getCategory());
        return dgraphToken;
    }

    private DgraphBin convertBin(String bin) {
        DgraphBin dgraphBin = new DgraphBin();
        dgraphBin.setBin(bin);
        return dgraphBin;
    }

    private DgraphCountry toDgraphCountryObject(String counrty) {
        DgraphCountry country = new DgraphCountry();
        country.setCountryName(counrty);
        return country;
    }

}

package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.fraudbusters.factory.properties.OperationProperties;
import com.rbkmoney.fraudbusters.fraud.constant.DgraphEntity;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudo.model.TimeWindow;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DgraphTestAggregationUtils {

    public static TimeWindow createTestTimeWindow() {
        return TimeWindow.builder()
                .startWindowTime(600_000L)
                .endWindowTime(0L)
                .build();
    }

    public static Map<DgraphEntity, Set<PaymentCheckedField>> createTestUsualDgraphEntityMap(
            DgraphEntity dgraphEntity,
            PaymentCheckedField paymentCheckedField
    ) {
        return Map.of(
                dgraphEntity, Set.of(paymentCheckedField),
                DgraphEntity.PARTY, Set.of(PaymentCheckedField.PARTY_ID),
                DgraphEntity.SHOP, Set.of(PaymentCheckedField.SHOP_ID)
        );
    }

    public static Map<DgraphEntity, Set<PaymentCheckedField>> createTestFullDgraphEntityMap() {
        Map<DgraphEntity, Set<PaymentCheckedField>> dgraphEntitySetMap = new HashMap<>();
        dgraphEntitySetMap.put(DgraphEntity.BIN, Set.of(PaymentCheckedField.BIN));
        dgraphEntitySetMap.put(DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN, PaymentCheckedField.PAN));
        dgraphEntitySetMap.put(DgraphEntity.PARTY, Set.of(PaymentCheckedField.PARTY_ID));
        dgraphEntitySetMap.put(DgraphEntity.SHOP, Set.of(PaymentCheckedField.SHOP_ID));
        dgraphEntitySetMap.put(DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.MOBILE, PaymentCheckedField.RECURRENT));
        dgraphEntitySetMap.put(DgraphEntity.COUNTRY, Set.of(PaymentCheckedField.COUNTRY_BANK));
        dgraphEntitySetMap.put(DgraphEntity.CURRENCY, Set.of(PaymentCheckedField.CURRENCY));
        dgraphEntitySetMap.put(DgraphEntity.EMAIL, Set.of(PaymentCheckedField.EMAIL));
        dgraphEntitySetMap.put(DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT));
        dgraphEntitySetMap.put(DgraphEntity.IP, Set.of(PaymentCheckedField.IP));
        return dgraphEntitySetMap;
    }

    public static OperationProperties createDefaultOperationProperties() {
        return OperationProperties.builder()
                .tokenId("newToken")
                .maskedPan("0070")
                .email("test2@test.com")
                .fingerprint("newFinger")
                .partyId("newParty")
                .shopId("newShop")
                .bin("010101")
                .ip("newLocalhost")
                .country("Limonia")
                .eventTimeDispersion(true)
                .build();
    }

    public static OperationProperties createDefaultOperationProperties(PaymentModel paymentModel) {
        return OperationProperties.builder()
                .tokenId(paymentModel.getCardToken())
                .maskedPan(paymentModel.getPan())
                .email(paymentModel.getEmail())
                .fingerprint(paymentModel.getFingerprint())
                .partyId(paymentModel.getPartyId())
                .shopId(paymentModel.getShopId())
                .bin(paymentModel.getBin())
                .ip(paymentModel.getIp())
                .country(paymentModel.getBinCountryCode())
                .eventTimeDispersion(true)
                .build();
    }

    public static PaymentModel createTestPaymentModel() {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setBin("000000");
        paymentModel.setPan("2424");
        paymentModel.setBinCountryCode("Russia");
        paymentModel.setCardToken("token001");
        paymentModel.setPartyId("party1");
        paymentModel.setShopId("shop1");
        paymentModel.setTimestamp(System.currentTimeMillis());
        paymentModel.setMobile(false);
        paymentModel.setRecurrent(true);
        paymentModel.setEmail("test@test.ru");
        paymentModel.setIp("localhost");
        paymentModel.setFingerprint("finger001");
        paymentModel.setCurrency("RUB");
        return paymentModel;
    }

}

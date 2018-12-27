package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.proxy_inspector.InvoicePayment;
import com.rbkmoney.damsel.proxy_inspector.Party;
import com.rbkmoney.damsel.proxy_inspector.Shop;
import com.rbkmoney.damsel.proxy_inspector.*;
import com.rbkmoney.fraudo.model.FraudModel;

public class BeanUtil {


    public static final String FINGERPRINT = "fingerprint";
    public static final String SHOP_ID = "shopId";
    public static final String PARTY_ID = "partyId";
    public static final String IP = "ip";
    public static final String EMAIL = "email";
    public static final String BIN = "bin";
    public static final String SUFIX = "_2";

    public static Context createContext() {
        String pId = "pId";
        return createContext(pId);
    }

    public static Context createContext(String pId) {
        ContactInfo contact_info = new ContactInfo();
        contact_info.setEmail(EMAIL);
        return new Context(
                new PaymentInfo(
                        new Shop("2035728",
                                new Category("pizza", "no category"),
                                new ShopDetails("pizza-sushi"),
                                new ShopLocation() {{
                                    setUrl("http://www.pizza-sushi.com/");
                                }}
                        ),
                        new InvoicePayment(pId,
                                "",
                                Payer.customer(
                                        new CustomerPayer("custId", "1", "rec_paym_tool", createBankCard(),
                                                contact_info)),
                                new Cash(
                                        9000000000000000000L,
                                        new CurrencyRef("RUB")
                                )),
                        new com.rbkmoney.damsel.proxy_inspector.Invoice(
                                "iId",
                                "",
                                "",
                                new InvoiceDetails("drugs guns murder")),
                        new Party(pId)
                )
        );

    }

    public static Payer createRecurrentPayer() {
        return Payer.recurrent(new RecurrentPayer(createBankCard(), new RecurrentParentPayment("invoiceId", "paymentId"), new ContactInfo()));
    }

    public static Payer createCustomerPayer() {
        return Payer.customer(new CustomerPayer("custId", "1", "rec_paym_tool", createBankCard(), new ContactInfo()));

    }

    public static PaymentTool createBankCard() {
        return new PaymentTool() {{
            setBankCard(new BankCard(
                    "477bba133c182267fe5f086924abdc5db71f77bfc27f01f2843f2cdc69d89f05",
                    BankCardPaymentSystem.mastercard,
                    "424242",
                    "4242"
            ));
        }};
    }

    public static FraudModel createFraudModel() {
        FraudModel fraudModel = new FraudModel();
        fraudModel.setFingerprint(FINGERPRINT);
        fraudModel.setShopId(SHOP_ID);
        fraudModel.setPartyId(PARTY_ID);
        fraudModel.setIp(IP);
        fraudModel.setEmail(EMAIL);
        fraudModel.setBin(BIN);
        return fraudModel;
    }

    public static FraudModel createFraudModelSecond() {
        FraudModel fraudModel = new FraudModel();
        fraudModel.setFingerprint(FINGERPRINT + SUFIX);
        fraudModel.setShopId(SHOP_ID + SUFIX);
        fraudModel.setPartyId(PARTY_ID + SUFIX);
        fraudModel.setIp(IP + SUFIX);
        fraudModel.setEmail(EMAIL + SUFIX);
        fraudModel.setBin(BIN + SUFIX);
        return fraudModel;
    }
}
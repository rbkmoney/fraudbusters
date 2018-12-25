package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.proxy_inspector.*;
import com.rbkmoney.damsel.proxy_inspector.InvoicePayment;
import com.rbkmoney.damsel.proxy_inspector.Party;
import com.rbkmoney.damsel.proxy_inspector.Shop;

public class BeanUtil {


    public static Context createContext() {
        return new Context(
                new PaymentInfo(
                        new Shop("2035728",
                                new Category("pizza", "no category"),
                                new ShopDetails("pizza-sushi"),
                                new ShopLocation() {{
                                    setUrl("http://www.pizza-sushi.com/");
                                }}
                        ),
                        new InvoicePayment("pId",
                                "",
                                Payer.customer(
                                        new CustomerPayer("custId", "1", "rec_paym_tool", createBankCard(), new ContactInfo())),
                                new Cash(
                                        9000000000000000000L,
                                        new CurrencyRef("RUB")
                                )),
                        new com.rbkmoney.damsel.proxy_inspector.Invoice(
                                "iId",
                                "",
                                "",
                                new InvoiceDetails("drugs guns murder")),
                        new Party("ptId")
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
}
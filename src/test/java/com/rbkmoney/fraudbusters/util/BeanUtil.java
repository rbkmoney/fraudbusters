package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.damsel.proxy_inspector.Invoice;
import com.rbkmoney.damsel.proxy_inspector.InvoicePayment;
import com.rbkmoney.damsel.proxy_inspector.Party;
import com.rbkmoney.damsel.proxy_inspector.Shop;
import com.rbkmoney.damsel.proxy_inspector.*;
import com.rbkmoney.fraudo.model.FraudModel;
import com.rbkmoney.geck.common.util.TypeUtil;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;

public class BeanUtil {

    public static final String FINGERPRINT = "fingerprint";
    public static final String SHOP_ID = "shopId";
    public static final String PARTY_ID = "partyId";
    public static final String IP = "ip";
    public static final String EMAIL = "email";
    public static final String BIN = "bin";
    public static final String SUFIX = "_2";
    public static final Long AMOUNT_SECOND = 1000L;
    public static final Long AMOUNT_FIRST = 10500L;
    public static final String P_ID = "pId";
    public static final String ID_VALUE_SHOP = "2035728";
    public static final String BIN_COUNTRY_CODE = "RUS";

    public static Context createContext() {
        String pId = P_ID;
        return createContext(pId);
    }

    public static Context createContext(String pId) {
        ContactInfo contact_info = new ContactInfo();
        contact_info.setEmail(EMAIL);
        PaymentInfo payment = new PaymentInfo(
                new Shop(ID_VALUE_SHOP,
                        new Category("pizza", "no category"),
                        new ShopDetails("pizza-sushi"),
                        new ShopLocation() {{
                            setUrl("http://www.pizza-sushi.com/");
                        }}
                ),
                new InvoicePayment(pId,
                        TypeUtil.temporalToString(Instant.now()),
                        Payer.customer(
                                new CustomerPayer("custId", "1", "rec_paym_tool", createBankCard(),
                                        contact_info)),
                        new Cash(
                                9000L,
                                new CurrencyRef("RUB")
                        )),
                new Invoice(
                        "iId",
                        TypeUtil.temporalToString(Instant.now()),
                        "",
                        new InvoiceDetails("drugs guns murder")),
                new Party(pId)
        );
        return new Context(
                payment
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
            BankCard value = new BankCard(
                    "477bba133c182267fe5f086924abdc5db71f77bfc27f01f2843f2cdc69d89f05",
                    BankCardPaymentSystem.mastercard,
                    "424242",
                    "4242"
            );
            value.setIssuerCountry(Residence.RUS);
            setBankCard(value);
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
        fraudModel.setAmount(AMOUNT_FIRST);
        fraudModel.setBinCountryCode(BIN_COUNTRY_CODE);
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
        fraudModel.setAmount(AMOUNT_SECOND);
        fraudModel.setBinCountryCode(BIN_COUNTRY_CODE);
        return fraudModel;
    }

    @NotNull
    public static Command createGroupCommand(String localId, List<PriorityId> priorityIds) {
        Command command = new Command();
        Group group = new Group();
        group.setGroupId(localId);
        group.setTemplateIds(priorityIds);
        command.setCommandBody(CommandBody.group(group));
        command.setCommandType(com.rbkmoney.damsel.fraudbusters.CommandType.CREATE);
        return command;
    }

    @NotNull
    public static Command deleteGroupCommand(String localId, List<PriorityId> priorityIds) {
        Command command = new Command();
        Group group = new Group();
        group.setGroupId(localId);
        group.setTemplateIds(priorityIds);
        command.setCommandBody(CommandBody.group(group));
        command.setCommandType(CommandType.DELETE);
        return command;
    }

    @NotNull
    public static Command createGroupReferenceCommand(String party, String shopId, String idGroup) {
        Command command = new Command();
        command.setCommandType(CommandType.CREATE);
        command.setCommandBody(CommandBody.group_reference(new GroupReference()
                .setGroupId(idGroup)
                .setPartyId(party)
                .setShopId(shopId)));
        return command;
    }

    @NotNull
    public static Command createDeleteGroupReferenceCommand(String party, String shopId, String idGroup) {
        Command command = new Command();
        command.setCommandType(CommandType.DELETE);
        command.setCommandBody(CommandBody.group_reference(new GroupReference()
                .setGroupId(idGroup)
                .setPartyId(party)
                .setShopId(shopId)));
        return command;
    }
}
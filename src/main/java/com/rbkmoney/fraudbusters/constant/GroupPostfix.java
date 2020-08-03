package com.rbkmoney.fraudbusters.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GroupPostfix {

    public static final String TEMPLATE_GROUP_ID = "template-listener";
    public static final String TEMPLATE_P2P_GROUP_ID = "template-listener-p2p";

    public static final String GROUP_LIST_GROUP_ID = "group-listener";
    public static final String GROUP_P2P_LIST_GROUP_ID = "group-listener-p2p";

    public static final String GROUP_LIST_REFERENCE_GROUP_ID = "group-reference-listener";
    public static final String GROUP_P2P_LIST_REFERENCE_GROUP_ID = "group-reference-listener-p2p";

    public static final String REFERENCE_GROUP_ID = "reference-listener";
    public static final String REFERENCE_P2P_GROUP_ID = "reference-listener-p2p";

    public static final String RESULT_AGGREGATOR = "result-aggregator";
    public static final String P2P_RESULT_AGGREGATOR = "p2p-result-aggregator";
    public static final String PAYMENT_AGGREGATOR = "payment-aggregator";
    public static final String REFUND_AGGREGATOR = "refund-aggregator";
    public static final String CHARGEBACK_AGGREGATOR = "chargeback-aggregator";
    public static final String FRAUD_PAYMENT_AGGREGATOR = "fraud-payment-aggregator";

}

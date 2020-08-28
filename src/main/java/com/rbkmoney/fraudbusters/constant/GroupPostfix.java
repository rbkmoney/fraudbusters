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

}

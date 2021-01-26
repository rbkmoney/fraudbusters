package com.rbkmoney.fraudbusters.fraud.pool;

import java.util.List;

public interface CardTokenPool {

    void reinit(List<String> cardTokens);

    void clear();

    boolean isExist(String cardToken);

    boolean isEmpty();

}

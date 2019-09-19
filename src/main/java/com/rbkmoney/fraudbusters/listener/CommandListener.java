package com.rbkmoney.fraudbusters.listener;

import com.rbkmoney.damsel.fraudbusters.Command;

public interface CommandListener {

    void listen(Command command);

}

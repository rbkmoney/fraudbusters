package com.rbkmoney.fraudbusters.listener.event.sink;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceListener {

//    private final SourceEventParser eventParser;
//    private final MachineEventToMgEventSinkConverter converter;
//
//    @KafkaListener(topics = "${kafka.topics.invoicing}", containerFactory = "kafkaListenerContainerFactory")
//    public void listen(List<MachineEvent> messages, Acknowledgment ack) {
//        handle(messages);
//        ack.acknowledge();
//    }
//
//    private void handle(List<MachineEvent> machineEvents) {
//        machineEvents.stream()
//
//                .map(machineEvent -> Map.entry(machineEvent, eventParser.parseEvent(machineEvent)))
//                .filter(entry -> entry.getValue().isSetInvoiceChanges())
//                .map(entry -> {
//                            List<Map.Entry<MachineEvent, InvoiceChange>> invoiceChangesWithMachineEvent = new ArrayList<>();
//                            for (InvoiceChange invoiceChange : entry.getValue().getInvoiceChanges()) {
//                                invoiceChangesWithMachineEvent.add(Map.entry(entry.getKey(), invoiceChange));
//                            }
//                            return invoiceChangesWithMachineEvent;
//                        }
//                )
//                .flatMap(List::stream)
//                .map(kv -> converter.convert(kv.getKey(), kv.getValue()));
//    }
}

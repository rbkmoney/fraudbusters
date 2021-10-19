package com.rbkmoney.fraudbusters.dgraph;

import com.rbkmoney.damsel.fraudbusters.Resource;
import com.rbkmoney.damsel.fraudbusters.Withdrawal;
import com.rbkmoney.fraudbusters.serde.WithdrawalDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.rbkmoney.fraudbusters.factory.TestDgraphObjectFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ActiveProfiles("full-prod")
public class DgraphWithdrawalProcessingTest extends DgraphAbstractIntegrationTest {

    private static final String KAFKA_WITHDRAWAL_TOPIC = "withdrawal";

    @Test
    public void processRefundFromKafkaTest() throws Exception {
        Resource firstCryptoResource =
                Resource.crypto_wallet(generateTestCryptoWallet("qwe123", "wet"));
        produceWithdrawals(KAFKA_WITHDRAWAL_TOPIC, generateWithdrawals(5, firstCryptoResource));
        waitingTopic(KAFKA_WITHDRAWAL_TOPIC, WithdrawalDeserializer.class);

        Thread.sleep(15000L);
        assertEquals(0, getCountOfObjects("Token"));
        assertEquals(5, getCountOfObjects("Withdrawal"));
        assertEquals(0, getCountOfObjects("Bin"));
        assertEquals(1, getCountOfObjects("Country"));


        produceWithdrawals(KAFKA_WITHDRAWAL_TOPIC, generateWithdrawals(5, firstCryptoResource));
        Thread.sleep(15000L);
        assertEquals(0, getCountOfObjects("Token"));
        assertEquals(10, getCountOfObjects("Withdrawal"));
        assertEquals(0, getCountOfObjects("Bin"));
        assertEquals(1, getCountOfObjects("Country"));


        Resource firstDigitalResource =
                Resource.digital_wallet(generateTestDigitalWallet("qwe123", "prov-1"));
        produceWithdrawals(KAFKA_WITHDRAWAL_TOPIC, generateWithdrawals(3, firstDigitalResource));
        Thread.sleep(15000L);
        assertEquals(0, getCountOfObjects("Token"));
        assertEquals(13, getCountOfObjects("Withdrawal"));
        assertEquals(0, getCountOfObjects("Bin"));
        assertEquals(1, getCountOfObjects("Country"));

        Resource firstBankResource =
                Resource.bank_card(generateTestBankCard("token-1"));
        produceWithdrawals(KAFKA_WITHDRAWAL_TOPIC, generateWithdrawals(3, firstBankResource));
        Thread.sleep(15000L);
        assertEquals(1, getCountOfObjects("Token"));
        assertEquals(16, getCountOfObjects("Withdrawal"));
        assertEquals(1, getCountOfObjects("Bin"));
        assertEquals(1, getCountOfObjects("Country"));

        produceWithdrawals(KAFKA_WITHDRAWAL_TOPIC, generateWithdrawals(3, firstBankResource));
        Thread.sleep(15000L);
        assertEquals(1, getCountOfObjects("Token"));
        assertEquals(19, getCountOfObjects("Withdrawal"));
        assertEquals(1, getCountOfObjects("Bin"));
        assertEquals(1, getCountOfObjects("Country"));

        Resource secondBankResource =
                Resource.bank_card(generateTestBankCard("token-2"));
        produceWithdrawals(KAFKA_WITHDRAWAL_TOPIC, generateWithdrawals(3, secondBankResource));
        Thread.sleep(15000L);
        assertEquals(2, getCountOfObjects("Token"));
        assertEquals(22, getCountOfObjects("Withdrawal"));
        assertEquals(1, getCountOfObjects("Bin"));
        assertEquals(1, getCountOfObjects("Country"));
    }

    private List<Withdrawal> generateWithdrawals(int count, Resource destinationResource) {
        List<Withdrawal> withdrawals = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            withdrawals.add(generateWithdrawal(i, destinationResource));
        }
        return withdrawals;
    }

    void produceWithdrawals(String topicName, List<Withdrawal> withdrawals)
            throws InterruptedException, ExecutionException {
        try (Producer<String, Withdrawal> producer = createProducer()) {
            for (Withdrawal withdrawal : withdrawals) {
                ProducerRecord<String, Withdrawal> producerRecord =
                        new ProducerRecord<>(topicName, withdrawal.getId(), withdrawal);
                producer.send(producerRecord).get();
            }
        }
    }

}

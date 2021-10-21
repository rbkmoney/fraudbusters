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
        checkCountOfObjects("Withdrawal", 5);
        checkCountOfObjects("Token", 0);
        checkCountOfObjects("Bin", 0);
        checkCountOfObjects("Country", 1);

        produceWithdrawals(KAFKA_WITHDRAWAL_TOPIC, generateWithdrawals(5, firstCryptoResource));
        checkCountOfObjects("Withdrawal", 10);
        checkCountOfObjects("Token", 0);
        checkCountOfObjects("Bin", 0);
        checkCountOfObjects("Country", 1);

        Resource firstDigitalResource =
                Resource.digital_wallet(generateTestDigitalWallet("qwe123", "prov-1"));
        produceWithdrawals(KAFKA_WITHDRAWAL_TOPIC, generateWithdrawals(3, firstDigitalResource));
        checkCountOfObjects("Withdrawal", 13);
        checkCountOfObjects("Token", 0);
        checkCountOfObjects("Bin", 0);
        checkCountOfObjects("Country", 1);

        Resource firstBankResource =
                Resource.bank_card(generateTestBankCard("token-1"));
        produceWithdrawals(KAFKA_WITHDRAWAL_TOPIC, generateWithdrawals(3, firstBankResource));
        checkCountOfObjects("Withdrawal", 16);
        checkCountOfObjects("Token", 1);
        checkCountOfObjects("Bin", 1);
        checkCountOfObjects("Country", 1);

        produceWithdrawals(KAFKA_WITHDRAWAL_TOPIC, generateWithdrawals(3, firstBankResource));
        checkCountOfObjects("Withdrawal", 19);
        checkCountOfObjects("Token", 1);
        checkCountOfObjects("Bin", 1);
        checkCountOfObjects("Country", 1);

        Resource secondBankResource =
                Resource.bank_card(generateTestBankCard("token-2"));
        produceWithdrawals(KAFKA_WITHDRAWAL_TOPIC, generateWithdrawals(3, secondBankResource));
        checkCountOfObjects("Withdrawal", 22);
        checkCountOfObjects("Token", 2);
        checkCountOfObjects("Bin", 1);
        checkCountOfObjects("Country", 1);
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

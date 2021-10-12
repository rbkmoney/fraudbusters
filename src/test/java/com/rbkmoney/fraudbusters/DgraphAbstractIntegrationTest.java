package com.rbkmoney.fraudbusters;

import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.fraudbusters.extension.KafkaContainerExtension;
import com.rbkmoney.fraudbusters.extension.config.KafkaTopicsConfig;
import com.rbkmoney.fraudbusters.serde.PaymentDeserializer;
import com.rbkmoney.fraudbusters.util.KeyGenerator;
import com.rbkmoney.kafka.common.serialization.ThriftSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@Import(KafkaTopicsConfig.class)
@ExtendWith({
        SpringExtension.class,
        KafkaContainerExtension.class
})
@SpringBootTest(webEnvironment = RANDOM_PORT,
        classes = FraudBustersApplication.class,
        properties = {
                "kafka.listen.result.concurrency=1",
                "kafka.dgraph.topics.payment.enabled=true",
                "dgraph.port=9080",
                "dgraph.withAuthHeader=false"
        })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class DgraphAbstractIntegrationTest {

    private static GenericContainer dgraphServer;
    private static volatile boolean isDgraphStarted;
    private static String testHostname = "localhost";

    @DynamicPropertySource
    static void connectionConfigs(DynamicPropertyRegistry registry) {
        registry.add("kafka.bootstrap.servers", KafkaContainerExtension.KAFKA::getBootstrapServers);
        registry.add("dgraph.host", () -> testHostname);
    }

    @BeforeAll
    public static void setup() throws Exception {
        if (!isDgraphStarted) {
            startDgraphServer();
            cleanupBeforeTermination();
            isDgraphStarted = true;
        }
    }

    private static void cleanupBeforeTermination() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (isDgraphStarted) {
                if (dgraphServer.isRunning()) {
                    dgraphServer.stop();
                }
            }
        }));
    }

    private static void startDgraphServer() throws InterruptedException {
        log.info("Creating dgraph server");
        dgraphServer = new GenericContainer<>("dgraph/standalone:latest")
                .withExposedPorts(8000, 8080, 9080)
                .withPrivilegedMode(true)
                .waitingFor(new WaitAllStrategy()
                        .withStartupTimeout(Duration.ofMinutes(2)));
        dgraphServer.setPortBindings(Arrays.asList("8000:8000", "8080:8080", "9080:9080"));
        dgraphServer.start();
        testHostname = dgraphServer.getHost();
        log.info("Dgraph server was created (host:{}, ip: {}, containerIp: {}, ports: {})", testHostname,
                dgraphServer.getIpAddress(), dgraphServer.getContainerIpAddress(), dgraphServer.getExposedPorts());
        Thread.sleep(5000);

    }

    public Producer<String, Payment> createPaymentProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaContainerExtension.KAFKA.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, KeyGenerator.generateKey("client_id_"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, new ThriftSerializer<Payment>().getClass());
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
        return new KafkaProducer<>(props);
    }

    public Consumer<String, Payment> createConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaContainerExtension.KAFKA.getBootstrapServers());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, PaymentDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new KafkaConsumer<>(props);
    }
}

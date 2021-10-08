package com.rbkmoney.fraudbusters.config.dgraph;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rbkmoney.kafka.common.retry.ConfigurableRetryPolicy;
import io.dgraph.DgraphClient;
import io.dgraph.DgraphGrpc;
import io.dgraph.DgraphProto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Collections;

import static com.rbkmoney.fraudbusters.constant.SchemaConstants.SCHEMA;

@Slf4j
@Configuration
public class DgraphConfig {

    @Value("${dgraph.maxAttempts}")
    private int maxAttempts;

    @Bean
    public ObjectMapper dgraphObjectMapper() {
        return new com.fasterxml.jackson.databind.ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule())
                .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Bean
    public RetryTemplate dgraphRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(
                new ConfigurableRetryPolicy(maxAttempts, Collections.singletonMap(RuntimeException.class, true))
        );
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(10L);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        return retryTemplate;
    }

    @Bean
    public DgraphClient dgraphClient(@Value("${dgraph.host}") String host,
                                     @Value("${dgraph.port}") int port,
                                     @Value("${dgraph.withAuthHeader}") boolean withAuthHeader) {
        log.info("Create dgraph client (host: {}, port: {})", host, port);
        DgraphClient dgraphClient = new DgraphClient(createStub(host, port, withAuthHeader));
        log.info("Dgraph client was created (host: {}, port: {})", host, port);
        dgraphClient.alter(
                DgraphProto.Operation.newBuilder()
                        .setDropAll(true)
                        .setSchema(SCHEMA)
                        .build()
        );
        // duplicate for syntax check
        dgraphClient.alter(
                DgraphProto.Operation.newBuilder()
                        .setSchema(SCHEMA)
                        .build()
        );
        return dgraphClient;
    }

    private DgraphGrpc.DgraphStub createStub(String host, int port, boolean withAuthHeader) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();
        DgraphGrpc.DgraphStub stub = DgraphGrpc.newStub(channel);

        if (withAuthHeader) {
            Metadata metadata = new Metadata();
            metadata.put(
                    Metadata.Key.of("auth-token", Metadata.ASCII_STRING_MARSHALLER), "the-auth-token-value");
            stub = MetadataUtils.attachHeaders(stub, metadata);
        }
        return stub;
    }

}

package com.rbkmoney.fraudbusters.template;

import com.rbkmoney.fraudbusters.constant.Level;
import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import com.rbkmoney.fraudbusters.factory.stream.ConcreteTemplateStreamFactory;
import com.rbkmoney.fraudbusters.factory.stream.GlobalStreamFactory;
import com.rbkmoney.fraudbusters.fraud.FraudContextParser;
import com.rbkmoney.fraudbusters.template.pool.StreamPool;
import com.rbkmoney.fraudo.FraudoParser;
import org.apache.kafka.streams.KafkaStreams;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Properties;

public class TemplateDispatcherTest {

    private static final String TEMPLATE = "rule: 3 > 2 AND 1 = 1\n" +
            "-> accept;";
    public static final String LOCAL_ID = "localId";
    @Mock
    private Properties fraudStreamProperties;
    @Mock
    private GlobalStreamFactory globalStreamFactory;
    @Mock
    private ConcreteTemplateStreamFactory concreteTemplateStreamFactory;
    @Mock
    private FraudContextParser fraudContextParser;
    @Mock
    private StreamPool pool;
    @Mock
    private FraudoParser.ParseContext parseContext;
    @Mock
    private KafkaStreams kafkaStreams;


    TemplateDispatcher templateDispatcher;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(fraudContextParser.parse(TEMPLATE)).thenReturn(parseContext);
        templateDispatcher = new TemplateDispatcherImpl(fraudStreamProperties, globalStreamFactory,
                concreteTemplateStreamFactory, fraudContextParser, pool);
    }

    @Test
    public void doDispatchGlobal() {
        RuleTemplate ruleTemplate = RuleTemplate.builder()
                .lvl(Level.GLOBAL)
                .template(TEMPLATE)
                .build();
        Mockito.when(globalStreamFactory.create(fraudStreamProperties, parseContext)).thenReturn(kafkaStreams);

        templateDispatcher.doDispatch(ruleTemplate);
        Mockito.verify(pool, Mockito.times(1)).add(Level.GLOBAL.toString(), kafkaStreams);
    }

    @Test
    public void doDispatchConcrete() {
        RuleTemplate ruleTemplate = RuleTemplate.builder()
                .lvl(Level.CONCRETE)
                .localId(LOCAL_ID)
                .template(TEMPLATE)
                .build();
        Mockito.when(concreteTemplateStreamFactory.create(fraudStreamProperties, parseContext, LOCAL_ID)).thenReturn(kafkaStreams);

        templateDispatcher.doDispatch(ruleTemplate);
        Mockito.verify(pool, Mockito.times(1)).add(LOCAL_ID, kafkaStreams);
    }
}
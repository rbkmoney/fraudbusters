package com.rbkmoney.fraudbusters.template;

import com.rbkmoney.fraudbusters.constant.CommandType;
import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import com.rbkmoney.fraudbusters.fraud.FraudContextParser;
import com.rbkmoney.fraudbusters.stream.TemplateStreamFactoryImpl;
import com.rbkmoney.fraudbusters.template.pool.RuleTemplatePool;
import com.rbkmoney.fraudbusters.template.pool.RuleTemplatePoolImpl;
import com.rbkmoney.fraudo.FraudoParser;
import org.apache.kafka.streams.KafkaStreams;
import org.junit.Assert;
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
    private TemplateStreamFactoryImpl globalStreamFactory;
    @Mock
    private FraudContextParser fraudContextParser;
    @Mock
    private FraudoParser.ParseContext parseContext;
    @Mock
    private KafkaStreams kafkaStreams;

    private RuleTemplatePool templatePool = new RuleTemplatePoolImpl();

    TemplateDispatcher templateDispatcher;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(fraudContextParser.parse(TEMPLATE)).thenReturn(parseContext);
        templateDispatcher = new TemplateDispatcherImpl(fraudContextParser, templatePool);
    }

    @Test
    public void doDispatchGlobal() {
        RuleTemplate ruleTemplate = new RuleTemplate();
        ruleTemplate.setCommandType(CommandType.UPDATE);
        ruleTemplate.setLvl(TemplateLevel.GLOBAL);
        ruleTemplate.setTemplate(TEMPLATE);
        Mockito.when(globalStreamFactory.create(fraudStreamProperties)).thenReturn(kafkaStreams);

        templateDispatcher.doDispatch(ruleTemplate);
        FraudoParser.ParseContext parseContext = templatePool.get(TemplateLevel.GLOBAL.toString());
        Assert.assertNotNull(parseContext);
    }

    @Test
    public void doDispatchConcrete() {
        RuleTemplate ruleTemplate = new RuleTemplate();
        ruleTemplate.setLvl(TemplateLevel.CONCRETE);
        ruleTemplate.setCommandType(CommandType.UPDATE);
        ruleTemplate.setTemplate(TEMPLATE);
        ruleTemplate.setLocalId(LOCAL_ID);
        templateDispatcher.doDispatch(ruleTemplate);
        FraudoParser.ParseContext parseContext = templatePool.get(LOCAL_ID);
        Assert.assertNotNull(parseContext);

        ruleTemplate.setCommandType(CommandType.DELETE);
        templateDispatcher.doDispatch(ruleTemplate);
        parseContext = templatePool.get(LOCAL_ID);
        Assert.assertNull(parseContext);
    }
}
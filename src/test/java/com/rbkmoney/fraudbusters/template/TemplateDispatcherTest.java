package com.rbkmoney.fraudbusters.template;

public class TemplateDispatcherTest {

//    private static final String TEMPLATE = "rule: 3 > 2 AND 1 = 1\n" +
//            "-> accept;";
//    public static final String LOCAL_ID = "localId";
//    @Mock
//    private Properties fraudStreamProperties;
//    @Mock
//    private GlobalStreamFactory globalStreamFactory;
//    @Mock
//    private ConcreteTemplateStreamFactory concreteTemplateStreamFactory;
//    @Mock
//    private FraudContextParser fraudContextParser;
//    @Mock
//    private StreamPool pool;
//    @Mock
//    private FraudoParser.ParseContext parseContext;
//    @Mock
//    private KafkaStreams kafkaStreams;
//
//    TemplateDispatcher templateDispatcher;
//
//    @Before
//    public void init() {
//        MockitoAnnotations.initMocks(this);
//        Mockito.when(fraudContextParser.parse(TEMPLATE)).thenReturn(parseContext);
//        templateDispatcher = new TemplateDispatcherImpl(fraudStreamProperties, globalStreamFactory,
//                concreteTemplateStreamFactory, fraudContextParser, pool);
//    }
//
//    @Test
//    public void doDispatchGlobal() {
//        RuleTemplate ruleTemplate = new RuleTemplate();
//        ruleTemplate.setCommandType(CommandType.UPDATE);
//        ruleTemplate.setLvl(TemplateLevel.GLOBAL);
//        ruleTemplate.setTemplate(TEMPLATE);
//        Mockito.when(globalStreamFactory.create(fraudStreamProperties, parseContext, pool)).thenReturn(kafkaStreams);
//
//        templateDispatcher.doDispatch(ruleTemplate);
//        Mockito.verify(pool, Mockito.times(1)).add(TemplateLevel.GLOBAL.toString(), kafkaStreams);
//    }
//
//    @Test
//    public void doDispatchConcrete() {
//        RuleTemplate ruleTemplate = new RuleTemplate();
//        ruleTemplate.setLvl(TemplateLevel.CONCRETE);
//        ruleTemplate.setCommandType(CommandType.UPDATE);
//        ruleTemplate.setTemplate(TEMPLATE);
//        ruleTemplate.setLocalId(LOCAL_ID);
//        Mockito.when(concreteTemplateStreamFactory.create(fraudStreamProperties, parseContext, LOCAL_ID)).thenReturn(kafkaStreams);
//        templateDispatcher.doDispatch(ruleTemplate);
//        Mockito.verify(pool, Mockito.times(1)).add(LOCAL_ID, kafkaStreams);
//
//        ruleTemplate.setCommandType(CommandType.DELETE);
//        templateDispatcher.doDispatch(ruleTemplate);
//        Mockito.verify(pool, Mockito.times(1)).stopAndRemove(LOCAL_ID);
//    }
}
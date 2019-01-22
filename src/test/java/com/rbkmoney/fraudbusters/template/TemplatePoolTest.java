package com.rbkmoney.fraudbusters.template;

import com.rbkmoney.fraudbusters.template.pool.StreamPool;
import com.rbkmoney.fraudbusters.template.pool.TemplatePool;
import org.apache.kafka.streams.KafkaStreams;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;

public class TemplatePoolTest {

    public static final String TEST = "test";
    StreamPool streamPool = new TemplatePool();

    @Test
    public void add() {
        KafkaStreams mockStreams = Mockito.mock(KafkaStreams.class);
        streamPool.add(TEST, mockStreams);
        KafkaStreams result = streamPool.get(TEST);
        Assert.assertEquals(mockStreams, result);
        Mockito.verify(mockStreams, Mockito.times(1)).start();
    }

    @Test
    public void addRestart() {
        KafkaStreams mockStreams = Mockito.mock(KafkaStreams.class);
        Mockito.when(mockStreams.state()).thenReturn(KafkaStreams.State.RUNNING);
        streamPool.add(TEST, mockStreams);
        KafkaStreams mockStreamsNew = Mockito.mock(KafkaStreams.class);
        streamPool.add(TEST, mockStreamsNew);

        Mockito.verify(mockStreams, Mockito.times(1)).start();
        Mockito.verify(mockStreams, Mockito.times(1)).close(any());

        KafkaStreams result = streamPool.get(TEST);
        Assert.assertEquals(mockStreamsNew, result);
        Mockito.verify(mockStreamsNew, Mockito.times(1)).start();
    }

    @Test
    public void clear() {
        KafkaStreams mockStreams = Mockito.mock(KafkaStreams.class);
        Mockito.when(mockStreams.state()).thenReturn(KafkaStreams.State.RUNNING);
        streamPool.add(TEST, mockStreams);
        KafkaStreams mockStreamsNew = Mockito.mock(KafkaStreams.class);
        streamPool.add(TEST, mockStreamsNew);
        streamPool.clear();

        Mockito.verify(mockStreams, Mockito.times(1)).close(any());
        Mockito.verify(mockStreamsNew, Mockito.times(1)).close(any());
    }



    @Test
    public void delete() {
        KafkaStreams mockStreams = Mockito.mock(KafkaStreams.class);
        Mockito.when(mockStreams.state()).thenReturn(KafkaStreams.State.RUNNING);
        streamPool.add(TEST, mockStreams);
        streamPool.stopAndRemove(TEST);
        Mockito.verify(mockStreams, Mockito.times(1)).close(any());
        KafkaStreams kafkaStreams = streamPool.get(TEST);
        Assert.assertNull(kafkaStreams);
    }
}
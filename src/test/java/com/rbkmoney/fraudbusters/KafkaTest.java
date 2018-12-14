package com.rbkmoney.fraudbusters;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class KafkaTest {


    @Test
    public void testUpdateCountForSelect() throws Exception {




//        ReadOnlyWindowStore<String, Long> windowStore =
//                streams.store("CountsWindowStore", QueryableStoreTypes.windowStore());
//
//        WindowStoreIterator<Long> iterator = windowStore.fetch("world", Instant.ofEpochMilli(0), Instant.now());
//        while (iterator.hasNext()) {
//            KeyValue<Long, Long> next = iterator.next();
//            long windowTimestamp = next.key;
//            System.out.println("Count of 'world' @ time " + windowTimestamp + " is " + next.value);
//        }
//        iterator.close();
    }


}
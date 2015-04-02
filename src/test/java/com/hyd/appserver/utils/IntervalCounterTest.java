package com.hyd.appserver.utils;

import org.junit.Test;

/**
 * (描述)
 *
 * @author 贺一丁
 */
public class IntervalCounterTest {

    @Test
    public void testCount() throws Exception {
        IntervalCounter counter = new IntervalCounter(10, 100);
        counter.add(0);
        counter.add(50);
        counter.add(1000);
        System.out.println(counter.getCountResult());
    }
}

package com.hyd.utilities;

import org.junit.Test;

/**
 * (描述)
 *
 * @author 贺一丁
 */
public class ExecutionTimerTest {

    @Test
    public void testToTimelineString() throws Exception {

        ExecutionTimer.snapshot("开始");
        Thread.sleep(1000);
        ExecutionTimer.snapshot("检查参数完毕");
        Thread.sleep(1000);
        ExecutionTimer.snapshot("查询数据库完毕");
        Thread.sleep(1000);
        ExecutionTimer.snapshot("生成返回值完毕");

        System.out.println(ExecutionTimer.toTimelineString());
    }
}

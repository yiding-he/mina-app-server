package com.hyd.appserver.snapshot;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * (描述)
 *
 * @author HeYiding
 */
public class Snapshot {

    private final Map<Long, ProcessorSnapshot> PROCESSORS = new ConcurrentHashMap<Long, ProcessorSnapshot>();

    /**
     * 接口处理完成
     *
     * @param sessionId 会话ID
     */
    public void processorFinished(long sessionId) {
        PROCESSORS.remove(sessionId);
    }

    /**
     * 接口处理开始
     *
     * @param sessionId    会话ID
     * @param functionName 接口名称
     */
    public void processStarted(long sessionId, String functionName) {
        PROCESSORS.put(sessionId, new ProcessorSnapshot(functionName));
    }

    /**
     * 获取当前正在处理的接口的快照
     *
     * @return 当前正在处理的接口的快照
     */
    public Map<Long, ProcessorSnapshot> getSnapshot() {
        synchronized (PROCESSORS) {
            HashMap<Long, ProcessorSnapshot> map = new HashMap<Long, ProcessorSnapshot>(PROCESSORS);
            return Collections.unmodifiableMap(map);
        }
    }
}

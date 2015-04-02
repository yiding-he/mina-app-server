package com.hyd.appserver.snapshot;

/**
 * (描述)
 *
 * @author HeYiding
 */
public class ProcessorSnapshot {

    private long startTime = System.currentTimeMillis();

    private String functionName;

    public ProcessorSnapshot(String functionName) {
        this.functionName = functionName;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }
}

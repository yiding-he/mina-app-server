package com.hyd.appserver.core;

import com.hyd.appserver.utils.IntervalCounter;

import java.util.Map;

/**
 * 记录单个 Action 的执行统计信息
 *
 * @author yiding.he
 */
public class ActionStatistics {
    
    private String actionName;          // Action 名称
    
    private long executionCount;        // Action 执行次数
    
    private long totalExecutionTime;    // Action 总共执行时间

    // 根据执行时间长短的分段计数器
    private IntervalCounter intervalCounter = new IntervalCounter(ServerStatistics.EXECUTION_DURATION_STAGES);

    public ActionStatistics(String actionName) {
        this.actionName = actionName;
    }

    public synchronized void addExecutionData(long executionDuration) {
        intervalCounter.add(executionDuration);
        executionCount++;
        totalExecutionTime += executionDuration;
    }

    public String getActionName() {
        return actionName;
    }

    public long getExecutionCount() {
        return executionCount;
    }

    public long getTotalExecutionTime() {
        return totalExecutionTime;
    }

    public Map<String, Integer> getCounters() {
        return intervalCounter.getCountResult();
    }
}

package com.hyd.appserver.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务器任务执行统计
 *
 * @author yiding.he
 */
public class ServerStatistics {

    // 记录接口执行统计信息
    private final Map<String, ActionStatistics> actionStaticsMap = new HashMap<String, ActionStatistics>();

    // 执行消耗时间分阶段进行统计
    public static final int[] EXECUTION_DURATION_STAGES = {100, 500, 1000, 5000, 10000};

    /**
     * 添加单次的 Action 执行数据
     *
     * @param actionName        Action 名称
     * @param executionDuration 执行时间
     */
    public void addExecutionData(String actionName, long executionDuration) {
        ActionStatistics actionStatistics = getActionStatistics(actionName);
        actionStatistics.addExecutionData(executionDuration);
    }

    // 获取指定 Action 的执行统计数据
    private ActionStatistics getActionStatistics(String actionName) {
        ActionStatistics actionStatistics = actionStaticsMap.get(actionName);
        if (actionStatistics == null) {
            synchronized (actionStaticsMap) {
                actionStatistics = actionStaticsMap.get(actionName);
                if (actionStatistics == null) {
                    actionStatistics = new ActionStatistics(actionName);
                    actionStaticsMap.put(actionName, actionStatistics);
                }
            }
        }
        return actionStatistics;
    }

    // 获取所有 Action 的执行统计数据
    public List<ActionStatistics> getAllActionStatistics() {
        return new ArrayList<ActionStatistics>(actionStaticsMap.values());
    }
}

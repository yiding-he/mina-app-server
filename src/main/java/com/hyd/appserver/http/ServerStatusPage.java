package com.hyd.appserver.http;

import com.hyd.appserver.core.ActionStatistics;
import com.hyd.appserver.core.AppServerCore;
import com.hyd.appserver.core.ServerStatistics;
import com.hyd.appserver.utils.StringUtils;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.IoServiceStatistics;
import org.apache.mina.core.session.IoSessionConfig;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * (description)
 *
 * @author yiding.he
 */
public class ServerStatusPage {

    private IoService ioService;

    private static final String page_pattern = "<html><head>" +
            "<title>服务器状态</title>" +
            "<link rel=\"stylesheet\" href=\"default.css\"/>" +
            "</head><body>" +
            "  <h1>操作系统状态 <span style='font-size=10pt'><a href='/'>返回</a></span></h1>" +
            "  <table border=\"1\">" +
            "    </tr></thead>" +
            "    <tbody>" +
            "     %s" +
            "    </tbody>" +
            "  </table>" +
            "" +
            "  <h1>服务器状态</h1>" +
            "  <table border=\"1\">" +
            "    <tbody>" +
            "     %s" +
            "    </tbody>" +
            "  </table>" +
            "" +
            "  <h1>接口执行统计</h1>" +
            "  %s" +
            "</body></html>";

    private static final String action_pattern = "<div><a name=\"%s\"></a>" +
            "  <h3>%s</h3>" +
            "  <div>执行次数：%d</div>" +
            "  <div>总执行时间（毫秒）：%d</div>" +
            "  <div>平均执行时间（毫秒）：%d</div>" +
            "  <div>执行时间分布：</div>" +
            "  <table border=\"1\">" +
            "    <thead><tr>" +
            "      <td>执行时间</td>" +
            "      <td>次数</td>" +
            "    </tr></thead>" +
            "    <tbody>" +
            "     %s" +
            "    </tbody>" +
            "  </table>" +
            "</div>";

    private static final String row_pattern = "<tr><td>%s</td><td>%s</td></tr>";

    private final AppServerCore serverCore;

    public ServerStatusPage(IoService service, AppServerCore core) {
        this.ioService = service;
        this.serverCore = core;
    }

    public String toString() {

        String sysinfo = mapToRows(generateSystemInfo());
        String iostat = mapToRows(generateIoStat());
        String actionstat = generateActionStats();

        return String.format(page_pattern, sysinfo, iostat, actionstat);
    }

    private Map<String, Object> generateSystemInfo() {
        Map<String, Object> systemInfo = new LinkedHashMap<String, Object>();

        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        systemInfo.put("操作系统", os.getName() + " (Version " + os.getVersion() + ")");
        systemInfo.put("架构", os.getArch());

        File[] roots = File.listRoots();
        for (File root : roots) {
            String name = "分区 " + root.getAbsolutePath();
            String space = "剩余 " + convertToMB(root.getUsableSpace()) +
                    "/" + convertToMB(root.getTotalSpace()) + " MB";
            systemInfo.put(name, space);
        }

        return systemInfo;
    }

    private String convertToMB(long number) {
        return String.format("%,.3f", number / 1024.0 / 1024.0);
    }

    private String mapToRows(Map<String, ?> ioStatMap) {
        String iostat = "";
        for (String key : ioStatMap.keySet()) {
            iostat += String.format(row_pattern, key, ioStatMap.get(key));
        }
        return iostat;
    }

    private String generateActionStats() {
        ServerStatistics serverStatistics = this.serverCore.getServerStatistics();
        StringBuilder sb = new StringBuilder();
        List<ActionStatistics> actionStats = serverStatistics.getAllActionStatistics();
        for (ActionStatistics actionStat : actionStats) {
            sb.append(generateActionStat(actionStat));
        }
        return sb.toString();
    }

    private String generateActionStat(ActionStatistics actionStat) {
        long count = actionStat.getExecutionCount();
        long time = actionStat.getTotalExecutionTime();
        String actionName = actionStat.getActionName();

        Map<String, Integer> counters = actionStat.getCounters();

        return String.format(action_pattern,
                actionName, actionName, count, time, (time / count), mapToRows(counters));
    }

    private Map<String, Object> generateIoStat() {
        Map<String, Object> statusMap = new LinkedHashMap<String, java.lang.Object>();

        statusMap.put("启动时间", StringUtils.dateToString(this.ioService.getActivationTime(), "yyyy-MM-dd HH:mm:ss"));

        Runtime runtime = Runtime.getRuntime();
        statusMap.put("已用内存", convertToMB(runtime.totalMemory() - runtime.freeMemory()) + " MB");

        statusMap.put("处理线程池大小", this.serverCore.getServerConfiguration().getMaxActiveWorkers());
        statusMap.put("当前连接数", this.ioService.getManagedSessionCount());

        IoServiceStatistics stat = this.ioService.getStatistics();
        statusMap.put("最大连接数", stat.getLargestManagedSessionCount());
        statusMap.put("已读入字节", String.format("%,d", stat.getReadBytes()));
        statusMap.put("已读入消息", String.format("%,d", stat.getReadMessages()));
        statusMap.put("已输出字节", String.format("%,d", stat.getWrittenBytes()));
        statusMap.put("已输出消息", String.format("%,d", stat.getWrittenMessages()));

        IoSessionConfig config = this.ioService.getSessionConfig();
        statusMap.put("空闲超时时间（秒）", config.getBothIdleTime());
        statusMap.put("读取缓冲区大小（字节）", String.format("%,d", config.getReadBufferSize()));

        return statusMap;
    }
}

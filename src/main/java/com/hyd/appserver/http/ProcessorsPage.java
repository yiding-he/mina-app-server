package com.hyd.appserver.http;

import com.hyd.appserver.snapshot.ProcessorSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * (description)
 *
 * @author yiding.he
 */
public class ProcessorsPage {

    private static final String page_pattern = "<html><head>" +
            "<title>线程池状态</title>" +
            "<link rel=\"stylesheet\" href=\"default.css\"/>" +
            "</head><body>" +
            "  <h1>线程池状态 <span style='font-size=10pt'><a href='/'>返回</a></span></h1>" +
            "  <table border=\"1\">" +
            "    <tr><td>会话ID</td><td>接口名</td><td>已处理时间(ms)</td>" +
            "    </tr></thead>" +
            "    <tbody>" +
            "     %s" +
            "    </tbody>" +
            "  </table>" +
            "</body></html>";

    private final Map<Long, ProcessorSnapshot> snapshot;

    public ProcessorsPage(Map<Long, ProcessorSnapshot> snapshot) {
        this.snapshot = snapshot;
    }

    public String toString() {
        String poolInfo = listToRows(generatePoolInfo());
        return String.format(page_pattern, poolInfo);
    }

    private String listToRows(List<List<String>> rows) {
        String result = "";

        for (List<String> row : rows) {
            String rowStr = "<tr>";

            for (String value : row) {
                rowStr += "<td>" + value + "</td>";
            }

            rowStr += "</tr>";
            result += rowStr;
        }

        return result;
    }

    private List<List<String>> generatePoolInfo() {
        List<List<String>> result = new ArrayList<List<String>>();

        for (Long sessionId : snapshot.keySet()) {
            List<String> row = new ArrayList<String>();
            ProcessorSnapshot info = snapshot.get(sessionId);

            row.add(String.valueOf(sessionId));
            row.add(info.getFunctionName());
            row.add(String.valueOf(System.currentTimeMillis() - info.getStartTime()));

            result.add(row);
        }

        return result;
    }
}

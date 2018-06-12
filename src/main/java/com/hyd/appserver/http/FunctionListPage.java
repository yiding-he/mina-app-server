package com.hyd.appserver.http;

import com.hyd.appserver.Action;
import com.hyd.appserver.annotations.AnnotationUtils;
import com.hyd.appserver.annotations.Function;
import com.hyd.appserver.utils.StringUtils;

import java.util.List;

/**
 * 接口列表页面
 *
 * @author yiding.he
 */
public class FunctionListPage {

    private static final String page_pattern = "<html><head>" +
            "<title>接口列表</title>" +
            "<meta name=\"viewport\" content=\"width=device-width, user-scalable=no\">" +
            "<link rel=\"stylesheet\" href=\"default.css\"/>" +
            "<link rel=\"stylesheet\" href=\"list.css\"/>" +
            "<script type=\"text/javascript\" src=\"jquery-1.6.js\"></script>" +
            "<script type=\"text/javascript\" src=\"list.js\"></script>" +
            "</head><body>" +
            "  <div class=\"header\">" +
            "    <h1>接口列表</h1>" +
            "    <div>搜索：<input type=\"text\" id=\"filter\" size=\"20\"/> &nbsp; " +
            "    <a href=\"action_creator.html\">创建 Action</a>" +
            "    <a href=\"status\">服务器状态</a>" +
            "    <a href=\"pool\">线程池状态</a>" +
            "    <a href=\"about.html\">关于</a>" +
            "  </div>" +
            "  </div>" +
            "  <div class=\"functions\">%s</div>" +
            "</body></html>";

    private static final String function_pattern = "<div class=\"function\">" +
            "<div class=\"function-name\"><a href=\"%s\">%s</a></div>" +
            "<div class=\"description\">%s</div>" +
            "</div>";

    private final List<Action> actionBeans;

    // 构造方法
    public FunctionListPage(List<Action> actionBeans) {
        this.actionBeans = actionBeans;
    }

    @Override
    public String toString() {
        return String.format(page_pattern, getFunctionListStr());
    }

    private String getFunctionListStr() {
        String result = "";

        synchronized (actionBeans) {
            for (Action actionBean : actionBeans) {
                Class<? extends Action> aClass = actionBean.getClass();
                Function function = AnnotationUtils.getFunction(aClass);
                String desc = function == null ? "" : function.description();
                String fullPath = actionBean.getFullFunctionPath();
                String linkUrl = "functions" + StringUtils.encodeUrl(fullPath);
                result += String.format(function_pattern, linkUrl, fullPath, desc);
            }
        }

        return result;
    }
}

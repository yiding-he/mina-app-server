package com.hyd.appserver.http;

import com.hyd.appserver.Action;
import com.hyd.appserver.annotations.AnnotationUtils;
import com.hyd.appserver.annotations.Function;

import java.util.Collections;
import java.util.Comparator;
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
            "  </div>" +
            "  </div>" +
            "  <div class=\"functions\">%s</div>" +
            "</body></html>";

    private static final String function_pattern = "<div class=\"function\">" +
            "<div class=\"function-name\"><a href=\"%s\">%s</a></div>" +
            "<div class=\"description\">%s</div>" +
            "</div>";

    private List<Class<Action>> actionClasses;

    public FunctionListPage(List<Class<Action>> actionClasses) {
        this.actionClasses = actionClasses;
        sortFunctionNames();
    }

    private void sortFunctionNames() {
        Collections.sort(this.actionClasses, new Comparator<Class<Action>>() {
            @Override
            public int compare(Class<Action> o1, Class<Action> o2) {
                return o1.getSimpleName().compareTo(o2.getSimpleName());
            }
        });
    }

    @Override
    public String toString() {
        return String.format(page_pattern, getFunctionListStr());
    }

    private String getFunctionListStr() {
        String result = "";

        synchronized (actionClasses) {
            for (Class<Action> aClass : actionClasses) {
                Function function = AnnotationUtils.getFunction(aClass);
                String desc = function == null ? "" : function.description();
                result += String.format(function_pattern, aClass.getSimpleName(), aClass.getSimpleName(), desc);
            }
        }

        return result;
    }
}

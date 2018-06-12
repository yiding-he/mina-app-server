package com.hyd.appserver.http;

import com.hyd.appserver.Action;
import com.hyd.appserver.ActionContext;
import com.hyd.appserver.annotations.*;
import com.hyd.appserver.utils.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 接口文档页面
 *
 * @author yiding.he
 */
public class FunctionPage {

    private static final String page_pattern = "<html><head>" +
            "<title>%s</title>" +
            "<meta name=\"viewport\" content=\"width=device-width, user-scalable=no\">" +
            "<link rel=\"stylesheet\" href=\"default.css\"/>" +
            "<link rel=\"stylesheet\" href=\"function.css\"/>" +
            "<style type=\"text/css\">%s</style>" +
            "</head><body>" +
            "<div style=\"margin:5px;\">" +
            "<a href=\"./\">接口列表</a> &gt; %s" +
            "</div>" +
            "<div class=\"result\"><strong>请求</strong><br/>%s</div>" +
            "<div class=\"result\"><strong>调用结果</strong><br/>%s</div>" +
            "%s" +
            "<div class=\"description\">" +
            "  <div class=\"field\"><div class=\"label\">路径</div>" +
            "    <div class=\"fieldvalue functionname\"><code><strong>%s</strong></code></div>" +
            "  </div>" +
            "  <div class=\"field\"><div class=\"label\">描述</div><div class=\"fieldvalue\">%s</div></div>" +
            "  <div class=\"field\"><div class=\"label\">参数（红色表示必填，日期格式可以是 yyyy-MM-dd、yyyy-MM-dd HH:mm 或 yyyy-MM-dd HH:mm:ss）</div>" +
            "    %s" +
            "  </div>" +
            "  <div class=\"field\"><div class=\"label\">返回值属性</div>%s</div>" +
            "</div>" +
            "</body></html>";

    public static final String stack_trace_pattern = "<div class=\"result\"><strong>服务器异常堆栈</strong><br/>%s</div>";

    public static final String param_form_pattern = "    <form action=\"%s\">%s" +
            "      <div class=\"param submit\"><input id=\"submit_button\" type=\"submit\" value=\"提交\"/></div>" +
            "    </form>";

    public static final String param_pattern = "<div class=\"param %s\">" +
            "  <span class=\"code\"><span class=\"codename\">%s</span> : <span class=\"type\">%s</span></span>" +
            "  - %s%s <input type=\"text\" name=\"%s\" value=\"%s\"/>" +
            "</div>";

    public static final String param_pattern_readonly = "<div class=\"param %s\">" +
            "  <span class=\"code\"><span class=\"codename\">%s</span> : <span class=\"type\">%s</span></span>" +
            "  - %s%s" +
            "</div>";

    public static final String property_pattern = "<div class=\"property\">" +
            "<span class=\"code\"><span class=\"codename\">%s</span> : <span class=\"type\">%s</span></span> - %s" +
            "</div>";

    public static final String list_property_pattern = "<div class=\"listproperty\">" +
            "<div class=\"property\" style=\"margin-left:0\"><span class=\"code\"><span class=\"codename\">%s</span> :" +
            " <span class=\"type\">%s</span></span> - %s</div>" +
            "%s" +
            "</div>";

    public static final String pojo_type_link_pattern = "<a href=\"pojo/%s\">%s</a>";

    /////////////////////////////////////////

    private String title;

    private String responseText;

    private String stackTrace;

    private String requestText;

    private Class<? extends Action> actionClass;

    private Map<String, List<String>> parameters;

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public void setRequestText(String requestText) {
        this.requestText = requestText;
    }

    public void setActionClass(Class<? extends Action> actionClass) {
        this.actionClass = actionClass;
    }

    public void setParameters(Map<String, List<String>> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        String actionName = getActionName();
        return String.format(page_pattern,
                this.title,
                getStyle(),
                actionName,
                this.requestText,
                getResponseText(),
                getStackTraceString(),
                actionName,
                getDescription(),
                getParameterFormStr(actionName),
                getResultStr()
        );
    }

    private String getStyle() {
        String style = "";

        if (!ActionContext.getContext().getServerConfiguration().isHttpTestEnabled()) {
            style += " #submit_button{display:none;}";
        }

        return style;
    }

    private String getResponseText() {
        return this.responseText
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br/>")
                .replace("\t", "    ")
                .replace(" ", "&nbsp;");
    }

    private String getStackTraceString() {
        if (StringUtils.isEmpty(this.stackTrace)) {
            return "";
        }

        return String.format(stack_trace_pattern, StringUtils.toHtml(stackTrace));
    }

    private String getParameterFormStr(String actionName) {
        String parameterStr = getParameterStr();

        if (parameterStr.length() == 0) {
            return "";
        } else {
            return String.format(param_form_pattern, StringUtils.encodeUrl("functions" + actionName), parameterStr);
        }
    }

    private String getParameterStr() {
        if (this.actionClass == null) {
            return "";
        }

        Function annotation = AnnotationUtils.getFunction(this.actionClass);

        if (annotation == null) {
            return "";
        }

        if (annotation.parameters().length == 0) {
            return "";
        }

        String str = "";
        for (Parameter parameter : annotation.parameters()) {
            String className = parameter.required() ? "required" : "optional";

            String optionalStr = parameter.required() ? "" : "，可选";

            String defaultValueStr = parameter.defaultValue().equals("") ?
                    "" : ("，缺省值 " + parameter.defaultValue());

            if (ActionContext.getContext().getServerConfiguration().isHttpTestEnabled()) {
                List<String> paramValues = parameters.get(parameter.name());
                String paramValue = (paramValues == null || paramValues.isEmpty()) ? "" : paramValues.get(0);
                str += String.format(param_pattern,
                        className,
                        parameter.name(),
                        parameter.type(),
                        parameter.description(),
                        optionalStr + defaultValueStr,
                        parameter.name(),
                        StringUtils.defaultIfEmpty(paramValue, "")
                );
            } else {
                str += String.format(param_pattern_readonly,
                        className,
                        parameter.name(),
                        parameter.type(),
                        parameter.description(),
                        optionalStr + defaultValueStr
                );
            }

        }

        return str;
    }

    private String getActionName() {
        if (this.actionClass == null) {
            return "";
        }

        Function function = AnnotationUtils.getFunction(this.actionClass);
        return function.path();
    }

    private String getDescription() {
        if (this.actionClass == null) {
            return "";
        }

        Function function = AnnotationUtils.getFunction(this.actionClass);
        return function.description();
    }

    private String getResultStr() {
        if (this.actionClass == null) {
            return "";
        }

        Function annotation = AnnotationUtils.getFunction(this.actionClass);

        if (annotation == null) {
            return "";
        }

        String str = "";
        Result result = annotation.result();

        str += "<div class=\"property return_value_title\">通用属性：</div>";
        str += String.format(property_pattern, "success", "Boolean", result.success());
        str += String.format(property_pattern, "message", "String", "如果 success 为 false，则表示错误信息");
        str += String.format(property_pattern, "code", "Integer", "0 表示成功，其他值表示失败");
        str += "<div class=\"property return_value_title\">自定义属性：</div>";

        for (Property property : result.properties()) {
            str += getPropertyStr(property);
        }

        for (ListProperty listProperty : result.listProperties()) {

            String type = "Map";
            if (listProperty.type() != Object.class) {
                type = getTypeLink(listProperty.type());
            }

            String meta = "";
            for (Property property : listProperty.properties()) {
                meta += getPropertyStr(property);
            }
            str += String.format(list_property_pattern, listProperty.name(),
                    ("List&lt;" + type + "&gt;"), listProperty.description(), meta);
        }

        return str;
    }


    private String getPropertyStr(Property property) {
        String property_str;

        if (property.type() != Type.Pojo) {
            property_str = String.format(property_pattern, property.name(), property.type(), property.description());
        } else {
            Class type = property.pojoType();
            String link = getTypeLink(type);
            property_str = String.format(property_pattern, property.name(), link, property.description());
        }
        return property_str;
    }

    private String getTypeLink(Class type) {
        return String.format(pojo_type_link_pattern, type.getName(), type.getSimpleName());
    }
}

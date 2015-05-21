package com.hyd.appserver.http;

import com.hyd.appserver.*;
import com.hyd.appserver.annotations.AnnotationUtils;
import com.hyd.appserver.annotations.Function;
import com.hyd.appserver.annotations.Parameter;
import com.hyd.appserver.annotations.Type;
import com.hyd.appserver.core.ClientInfo;
import com.hyd.appserver.core.Protocol;
import com.hyd.appserver.snapshot.ProcessorSnapshot;
import com.hyd.appserver.utils.IOUtils;
import com.hyd.appserver.utils.JsonUtils;
import com.hyd.appserver.utils.StringUtils;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.MessageHandler;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * (description)
 *
 * @author yiding.he
 */
public class HttpRequestHandler implements MessageHandler<HttpRequestMessage> {

    public static final Map<String, String> MIME_TYPES = new HashMap<String, String>();

    private static final String ETag = String.valueOf(System.currentTimeMillis());

    static {
        MIME_TYPES.put(".html", "text/html");
        MIME_TYPES.put(".ico", "image/ico");
        MIME_TYPES.put(".gif", "image/gif");
        MIME_TYPES.put(".png", "image/png");
        MIME_TYPES.put(".jpg", "image/jpg");
        MIME_TYPES.put(".js", "text/javascript");
        MIME_TYPES.put(".json", "application/javascript");
        MIME_TYPES.put(".css", "text/css");
    }

    private MinaAppServer server;

    public HttpRequestHandler(MinaAppServer server) {
        this.server = server;
    }

    public void handleMessage(IoSession ioSession, HttpRequestMessage requestMessage) throws Exception {
        HttpResponseMessage message = new HttpResponseMessage(requestMessage);

        // 用户请求依次分为以下几种情况：
        // 1、请求接口列表；
        // 2、请求返回值中定义的 ExposeablePojo 类的结构；
        // 3、调用接口；
        // 4、请求其他资源（js、css 等）。

        if (isFunctionListRequest(requestMessage)) {    // context = null 的情况在这里处理了，后面不会再遇到
            message.setContentType("text/html; charset=utf-8");
            message.appendBody(generateFunctionList());

        } else if (isServerStatusRequest(requestMessage)) {
            message.setContentType("text/html; charset=utf-8");
            message.appendBody(generateServerStatus(server.getMainAcceptor()));

        } else if (isThreadPoolRequest(requestMessage)) {
            message.setContentType("text/html; charset=utf-8");
            message.appendBody(generatePoolStatus(
                    server.getSnapshot().getSnapshot()));

        } else if (isPojoInfoRequest(requestMessage)) {
            message.setContentType("text/html; charset=utf-8");
            message.appendBody(generatePojoInfo(requestMessage));

        } else if (isFunctionCall(requestMessage)) {
            message.setContentType("text/html; charset=utf-8");
            Request request = parseRequest(requestMessage, ioSession);
            Response response = server.getCore().process(request, Protocol.Http);
            message.appendBody(generateFunctionCallResult(requestMessage, request, response));

        } else {
            processResourceRequest(requestMessage, message);
        }

        ioSession.write(message);
        ioSession.close(false);
    }

    private String generatePoolStatus(Map<Long, ProcessorSnapshot> snapshot) {
        return new ProcessorsPage(snapshot).toString();
    }

    private boolean isThreadPoolRequest(HttpRequestMessage requestMessage) {
        String context = requestMessage.getContext();
        return context.equals("pool");
    }

    private boolean isServerStatusRequest(HttpRequestMessage requestMessage) {
        String context = requestMessage.getContext();
        return context.equals("status");
    }

    private void processResourceRequest(
            HttpRequestMessage requestMessage, HttpResponseMessage message) throws Exception {

        // 简单的对浏览器设置缓存并判断
        if (requestMessage.getParameter("refresh").equals("") &&
                requestMessage.getHeader("If-None-Match") != null &&
                requestMessage.getHeader("If-None-Match").equals(ETag)) {
            message.setResponseCode(HttpResponseMessage.HTTP_STATUS_NOT_MODIFIED);
            return;
        }

        String path = requestMessage.getContext();
        byte[] content = findContent(path);
        if (content == null) {
            message.setResponseCode(HttpResponseMessage.HTTP_STATUS_NOT_FOUND);
        } else {
            message.setMimeType(getMimeType(path));
            message.setHeader("ETag", ETag);
            message.appendBody(content);
        }
    }

    private String getMimeType(String path) {
        for (String key : MIME_TYPES.keySet()) {
            if (path.endsWith(key)) {
                return MIME_TYPES.get(key);
            }
        }

        return "text/plain";
    }

    /////////////////////////////////////////

    // 生成服务器状态
    private String generateServerStatus(IoService service) {
        return new ServerStatusPage(service, this.server.getCore()).toString();
    }

    // 生成 POJO 类结构代码
    private String generatePojoInfo(HttpRequestMessage requestMessage) {
        String context = requestMessage.getContext();
        String className = context.substring("pojo/".length());

        try {
            Class type = Class.forName(className);
            return new PojoInfoPage(type).toString();
        } catch (ClassNotFoundException e) {
            return "未知类型：" + className;
        }
    }

    // 判断是否是请求 POJO 类结构信息
    private boolean isPojoInfoRequest(HttpRequestMessage requestMessage) {
        String context = requestMessage.getContext();
        return context.startsWith("pojo/");
    }

    // 判断是否是请求接口列表
    private boolean isFunctionListRequest(HttpRequestMessage requestMessage) {
        String context = requestMessage.getContext();
        return StringUtils.isEmpty(context);
    }

    // 返回其他资源内容
    private byte[] findContent(String context) throws Exception {
        if (StringUtils.isEmpty(context)) {
            context = "index.html";
        }
        String path = "/web/" + context;
        InputStream is = HttpRequestHandler.class.getResourceAsStream(path);

        if (is == null) {
            return null;
        } else {
            return IOUtils.readStreamAndClose(is);
        }
    }

    // 判断是否是请求接口调用
    private boolean isFunctionCall(HttpRequestMessage requestMessage) {
        String context = requestMessage.getContext();
        if (StringUtils.isEmpty(context)) {
            return false;
        }

        if (context.contains("?")) {
            context = context.substring(0, context.indexOf("?"));
        }
        return context.matches("^[a-zA-Z0-9_]+$");
    }

    // 返回接口列表
    private String generateFunctionList() {
        FunctionListPage page = new FunctionListPage(server.getCore().getActionClasses());
        return page.toString();
    }

    // 返回接口文档和调用结果
    private String generateFunctionCallResult(HttpRequestMessage requestMessage, Request request, Response response) {
        FunctionPage page = new FunctionPage();
        page.setTitle("接口 " + request.getFunctionName());
        page.setRequestText(request.getOriginalString());
        page.setRequest(requestMessage);
        page.setResponseText(jsonWithoutStacktrace(response));
        page.setStackTrace(response.getStackTrace());
        page.setActionClass(response.actionType);
        return page.toString();
    }

    // 获取 response 转换的不带异常堆栈的 json 字符串
    private String jsonWithoutStacktrace(Response response) {
        String stack = response.getStackTrace();
        response.setStackTrace(null);

        String result = JsonUtils.toJson(response, true);

        response.setStackTrace(stack);
        return result;
    }

    // 解析接口调用请求
    private Request parseRequest(HttpRequestMessage requestMessage, IoSession ioSession) {
        Request request = new Request();

        // function name
        if (!StringUtils.isEmpty(requestMessage.getContext())) {
            request.setFunctionName(requestMessage.getContext());
        } else {
            request.setFunctionName(requestMessage.getParameter("functionName"));
        }

        Class<Action> actionClass = this.server.getCore().getTypeMappings().find(request.getFunctionName());
        Function function = AnnotationUtils.getFunction(actionClass);

        Parameter[] funcParams = function == null ? new Parameter[]{} : function.parameters();

        // parameters
        List<String> parameterNames = requestMessage.getParameterNames();
        for (String parameterName : parameterNames) {

            Parameter paramDefinition = getParameterDefinition(funcParams, parameterName);
            String parameterValue = requestMessage.getParameter(parameterName);

            if (parameterValue == null && !paramDefinition.required()) {
                parameterValue = paramDefinition.defaultValue();
            }
            request.setParameter(parameterName, parameterValue);

            // 处理定义为数组的参数值
            processArrayParameter(request, parameterName, parameterValue, paramDefinition);
        }

        // client info
        ClientInfo info = request.getClientInfo();
        if (info == null) {
            info = new ClientInfo();
            request.setClientInfo(info);
        }

        info.setIpAddress(((InetSocketAddress) ioSession.getRemoteAddress()).getAddress().getHostAddress());

        request.setOriginalString(JsonUtils.toJson(request));
        return request;
    }

    // 处理可能被定义为数组的参数值
    private void processArrayParameter(Request request, String parameterName, String parameterValue, Parameter funcParam) {
        if (funcParam != null) {
            if (funcParam.type() == Type.StringArray) {
                request.setParameter(parameterName, parameterValue.split(","));

            } else if (funcParam.type() == Type.BooleanArray) {
                Boolean[] values = parseBooleanArray(parameterValue);
                request.setParameter(parameterName, values);

            } else if (funcParam.type() == Type.DateArray) {
                Date[] values = parseDateArray(parameterValue);
                request.setParameter(parameterName, values);

            } else if (funcParam.type() == Type.IntegerArray) {
                Integer[] values = parseIntegerArray(parameterValue);
                request.setParameter(parameterName, values);

            } else if (funcParam.type() == Type.DecimalArray) {
                Double[] values = parseDecimalArray(parameterValue);
                request.setParameter(parameterName, values);

            }
        }
    }

    private Double[] parseDecimalArray(String parameterValue) {
        String[] strValues = parameterValue.split(",");
        Double[] values = new Double[strValues.length];
        for (int i = 0; i < strValues.length; i++) {
            String strValue = strValues[i];
            if (strValue.trim().length() == 0) {
                values[i] = 0d;
            } else {
                values[i] = Double.parseDouble(strValue);
            }
        }
        return values;
    }

    private Integer[] parseIntegerArray(String parameterValue) {
        String[] strValues = parameterValue.split(",");
        Integer[] values = new Integer[strValues.length];
        for (int i = 0; i < strValues.length; i++) {
            String strValue = strValues[i];
            if (strValue.trim().length() == 0) {
                values[i] = 0;
            } else {
                values[i] = Integer.parseInt(strValue);
            }
        }
        return values;
    }

    private Date[] parseDateArray(String parameterValue) {
        String[] strValues = parameterValue.split(",");
        Date[] values = new Date[strValues.length];
        for (int i = 0; i < strValues.length; i++) {
            String strValue = strValues[i];
            if (strValue.trim().length() == 0) {
                values[i] = null;
            } else {
                values[i] = new Date(Integer.parseInt(strValue));
            }
        }
        return values;
    }

    private Boolean[] parseBooleanArray(String parameterValue) {
        String[] strValues = parameterValue.split(",");
        Boolean[] values = new Boolean[strValues.length];
        for (int i = 0; i < strValues.length; i++) {
            String strValue = strValues[i];
            if (strValue.trim().length() == 0) {
                values[i] = false;
            } else {
                values[i] = Boolean.valueOf(strValue);
            }
        }
        return values;
    }

    private Parameter getParameterDefinition(Parameter[] funcParams, String parameterName) {
        for (Parameter funcParam : funcParams) {
            if (funcParam.name().equals(parameterName)) {
                return funcParam;
            }
        }
        return null;
    }
}

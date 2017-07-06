package com.hyd.appserver.http;

import com.hyd.appserver.Action;
import com.hyd.appserver.AppServerException;
import com.hyd.appserver.MinaAppServer;
import com.hyd.appserver.Request;
import com.hyd.appserver.annotations.AnnotationUtils;
import com.hyd.appserver.annotations.Function;
import com.hyd.appserver.annotations.Parameter;
import com.hyd.appserver.annotations.Type;
import com.hyd.appserver.core.ClientInfo;
import com.hyd.appserver.core.Protocol;
import com.hyd.appserver.utils.IOUtils;
import com.hyd.appserver.utils.JsonUtils;
import com.hyd.appserver.utils.StringUtils;
import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yiding.he
 */
public class NanoHttpdServer extends NanoHTTPD {

    private MinaAppServer server;

    public NanoHttpdServer(MinaAppServer minaAppServer, String hostname, int port) {
        super(hostname, port);
        this.server = minaAppServer;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri().substring(1);

        if (uri.equals("")) {
            FunctionListPage page = new FunctionListPage(server.getCore().getActionClasses());
            return newFixedLengthResponse(page.toString());

        } else if (uri.equals("status")) {
            ServerStatusPage page = new ServerStatusPage(server.getMainAcceptor(), this.server.getCore());
            return newFixedLengthResponse(page.toString());

        } else if (uri.equals("pool")) {
            return newFixedLengthResponse(new ProcessorsPage(server.getSnapshot().getSnapshot()).toString());

        } else if (uri.startsWith("pojo/")) {
            String className = uri.substring("pojo/".length());

            try {
                Class type = Class.forName(className);
                return newFixedLengthResponse(new PojoInfoPage(type).toString());
            } catch (ClassNotFoundException e) {
                return newFixedLengthResponse("未知类型：" + className);
            }

        } else if (isFunctionCall(uri)) {
            try {
                Request request = parseRequest(session, uri);
                com.hyd.appserver.Response response = server.getCore().process(request, Protocol.Http);
                return newFixedLengthResponse(generateFunctionCallResult(session.getParameters(), request, response));
            } catch (Exception e) {
                throw new AppServerException(e);
            }
        }

        // resources
        String mimeType = "text/html";
        if (uri.endsWith(".css")) {
            mimeType = "text/css";
        } else if (uri.endsWith(".js")) {
            mimeType = "applicaton/javascript";
        } else if (uri.endsWith(".jpg") || uri.endsWith(".png") || uri.endsWith(".gif")) {
            mimeType = "image";
        }

        return newFixedLengthResponse(Response.Status.OK, mimeType, findContent0(uri));
    }

    // 返回接口文档和调用结果
    private String generateFunctionCallResult(Map<String, List<String>> parameters, Request request, com.hyd.appserver.Response response) {
        FunctionPage page = new FunctionPage();
        page.setTitle("接口 " + request.getFunctionName());
        page.setRequestText(request.getOriginalString());
        page.setParameters(parameters);
        page.setResponseText(jsonWithoutStacktrace(response));
        page.setStackTrace(response.getStackTrace());
        page.setActionClass(response.actionType);
        return page.toString();
    }

    // 获取 response 转换的不带异常堆栈的 json 字符串
    private String jsonWithoutStacktrace(com.hyd.appserver.Response response) {
        String stack = response.getStackTrace();
        response.setStackTrace(null);

        String result = JsonUtils.toJson(response, true);

        response.setStackTrace(stack);
        return result;
    }

    // 解析接口调用请求
    private Request parseRequest(IHTTPSession session, String uri) {

        if (uri.contains("?")) {
            uri = uri.substring(0, uri.indexOf("?"));
        }

        Request request = new Request();
        request.setFunctionName(uri);

        Class<Action> actionClass = this.server.getCore().getTypeMappings().find(request.getFunctionName());
        Function function = AnnotationUtils.getFunction(actionClass);

        Parameter[] funcParams = function == null ? new Parameter[]{} : function.parameters();

        // parameters
        Map<String, List<String>> parameters = session.getParameters();
        List<String> parameterNames = new ArrayList<>(parameters.keySet());
        for (String parameterName : parameterNames) {

            Parameter paramDefinition = getParameterDefinition(funcParams, parameterName);
            if (paramDefinition == null) {
                continue;
            }

            List<String> parameterValues = parameters.get(parameterName);
            String parameterValue = parameterValues.isEmpty()? null: parameterValues.get(0);

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

        info.setIpAddress(session.getRemoteIpAddress());

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

    private boolean isFunctionCall(String context) {
        if (context.contains("?")) {
            context = context.substring(0, context.indexOf("?"));
        }
        return context.matches("^[a-zA-Z0-9_]+$");
    }

    private String findContent0(String context) {
        try {
            return new String(findContent(context), "UTF-8");
        } catch (IOException e) {
            throw new AppServerException(e);
        }
    }

    // 返回其他资源内容
    private byte[] findContent(String context) throws IOException {
        if (StringUtils.isEmpty(context)) {
            context = "index.html";
        }
        String path = "/web/" + context;
        InputStream is = getClass().getResourceAsStream(path);

        if (is == null) {
            return null;
        } else {
            return IOUtils.readStreamAndClose(is);
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

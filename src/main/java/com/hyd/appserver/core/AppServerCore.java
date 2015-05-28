package com.hyd.appserver.core;

import com.hyd.appserver.*;
import com.hyd.appserver.annotations.AnnotationUtils;
import com.hyd.appserver.annotations.Function;
import com.hyd.appserver.annotations.Parameter;
import com.hyd.appserver.utils.ClassUtils;
import com.hyd.appserver.utils.JsonUtils;
import com.hyd.appserver.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 应用服务器业务逻辑处理，与外层协议无关
 *
 * @author yiding.he
 */
@SuppressWarnings("unchecked")
public class AppServerCore {

    // 本类使用的 logger
    static final Logger log = LoggerFactory.getLogger(AppServerCore.class);

    // 专用于输入/输出日志的 logger
    private static final Logger REQUEST_LOGGER = LoggerFactory.getLogger("com.hyd.appserver.log.request");

    private static final Logger RESPONSE_LOGGER = LoggerFactory.getLogger("com.hyd.appserver.log.response");

    /**
     * 缓存“类型-接口名”匹配关系
     */
    private FunctionTypeMappings<Action> typeMappings = new DefaultFunctionTypeMappings<Action>();

    /**
     * Action 工厂
     */
    private ActionFactory actionFactory = new DefaultActionFactory();

    /**
     * 自定义日志处理
     */
    private LogHandler logHandler = null;

    /**
     * 拦截器
     */
    private InterceptorChain interceptors = new InterceptorChain();

    /**
     * 服务器配置
     */
    private ServerConfiguration serverConfiguration;

    /**
     * 服务器统计信息
     */
    private ServerStatistics serverStatistics = new ServerStatistics();

    /**
     * 如果 enabled 为 false，将拒绝一切请求。服务器关闭之前需要先切断业务处理，拒绝所有后续请求。
     */
    private boolean enabled = true;

    public AppServerCore(ServerConfiguration configuration) {
        this.serverConfiguration = configuration;
    }

    public ServerStatistics getServerStatistics() {
        return serverStatistics;
    }

    public ActionFactory getActionFactory() {
        return actionFactory;
    }

    public FunctionTypeMappings<Action> getTypeMappings() {
        return typeMappings;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public ServerConfiguration getServerConfiguration() {
        return serverConfiguration;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 设置 Action 类所在的包
     *
     * @param packages Action 类所在的包
     */
    public void setPackages(String[] packages) {
        this.typeMappings.setPackages(packages);
    }

    /**
     * 设置 Action 对象工厂
     *
     * @param actionFactory Action 对象工厂
     */
    public void setActionFactory(ActionFactory actionFactory) {
        this.actionFactory = actionFactory;
    }

    /**
     * 设置接口调用日志处理类
     *
     * @param logHandler 接口调用日志处理类
     */
    public void setLogHandler(LogHandler logHandler) {
        this.logHandler = logHandler;
    }

    /**
     * 处理业务请求并返回结果
     *
     * @param _request 请求
     * @param protocol 请求类型
     *
     * @return 回应
     *
     * @throws Exception 如果处理失败
     */
    public Response process(final Request _request, Protocol protocol) throws Exception {

        if (!this.enabled) {
            return Response.fail("App server is shutting down...", -999999);
        }

        // 设置上下文
        ActionContext actionContext = ActionContext.getContext();
        if (actionContext == null) {
            actionContext = new ActionContext();
            ActionContext.setContext(actionContext);
        }
        actionContext.setProtocol(protocol);
        actionContext.setServerConfiguration(serverConfiguration);
        ActionContext.setContext(actionContext);

        /////////////////////////////////////////////////////////

        // 特殊命令：shutdown
        if (_request.getFunctionName().equals("__shutdown__")) {

            log.error("Server is shutting down by 'shutdown' command...");
            final AppServerCore core = this;
            core.setEnabled(false);

            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // nothing to do
                    }

                    MinaAppServer.shutdown(core);
                }
            }.start();
            return Response.success("Server will shutdown now.");
        }

        // 特殊命令：snapshot
        if (_request.getFunctionName().equals("__snapshot__")) {

            MinaAppServer server = MinaAppServer.getInstance(this);
            if (server == null) {
                return Response.fail("没有找到关联的 MinaAppServer 实例");
            }

            int runningProcessors = server.getSnapshot().getSnapshot().size();
            int activeConnections = server.getMainAcceptor().getManagedSessionCount();

            return Response.success()
                    .put("runningProcessors", runningProcessors)
                    .put("activeConnections", activeConnections);
        }

        /////////////////////////////////////////////////////////

        log.debug("Request: " + JsonUtils.toJson(_request));

        // request 的内容会被改变（放入参数缺省值），所以必须创建一个副本。
        Request request = Request.clone(_request);
        actionContext.setRequest(request);

        // 处理请求
        String className = request.getFunctionName();
        Class<Action> type = findClass(className);
        Response response;

        try {
            if (type == null) {
                response = Response.fail("(未知的接口'" + className + "')");

            } else {
                request = setupDefaultParameters(request, type);
                response = process0(request, type);
            }

        } catch (Throwable e) {
            log.error("服务器错误", e);
            response = handleException(e);
        }

        response.actionType = type;
        log.debug("Response: " + JsonUtils.toJson(response));

        actionContext.setResponse(response);    // 处理结果放入上下文
        outputExecutionInfo(actionContext);     // 通过 logger 输出接口调用日志
        addActionStatistics(actionContext);     // 添加统计信息

        // 自定义日志处理。这里需要完整的上下文，所以放在最后
        if (logHandler != null) {
            LogHandlerExecutor.executeHandler(logHandler, actionContext);
        }

        return response;
    }

    // 输出接口调用日志
    private void outputExecutionInfo(ActionContext actionContext) {
        long executeTime = actionContext.getExecutionEndMillis() - actionContext.getExecutionStartMillis();

        REQUEST_LOGGER.debug("request: " + JsonUtils.toJson(actionContext.getRequest()));
        RESPONSE_LOGGER.debug("response: " + JsonUtils.toJson(actionContext.getResponse()) + "; time: " + executeTime);
    }

    // 根据异常信息生成 response
    private Response handleException(Throwable e) {
        Class type = e.getClass();

        while (type != Object.class) {
            type = type.getSuperclass();
        }

        return Response.fail("服务器错误:" + e.toString());
    }

    /**
     * 根据 Action 文档定义，对请求设置参数的缺省值，
     * 这样当 Action 类从 Request 中获取参数时，就可以直接获取到缺省值了。
     *
     * @param request 请求
     * @param type    接口实现类
     *
     * @return request
     */
    private Request setupDefaultParameters(Request request, Class<Action> type) {
        Function function = AnnotationUtils.getFunction(type);

        if (function == null) {
            return request;
        }

        for (Parameter parameter : function.parameters()) {

            // 只有当参数是可选的才会需要设置缺省值。
            if (parameter.required()) {
                continue;
            }

            // 如果文档中没有定义缺省值，则不需要设置缺省值。
            if (parameter.defaultValue() == null) {
                continue;
            }

            // 如果用户传值不为空，则不需要设置缺省值。
            String name = parameter.name();
            if (!isParamValueEmpty(request, name)) {
                continue;
            }

            request.setParameter(name, parameter.defaultValue());
        }

        return request;
    }

    // 判断参数值是否为空
    private boolean isParamValueEmpty(Request request, String name) {
        Map<String, String[]> parameters = request.getParameters();

        return !parameters.containsKey(name) ||
                parameters.get(name) == null ||
                parameters.get(name).length == 0 ||
                StringUtils.isEmpty(parameters.get(name)[0]);
    }

    /**
     * 处理请求
     *
     * @param request 请求
     * @param type    Action 类型
     *
     * @return 处理结果
     */
    private Response process0(Request request, Class<Action> type) {
        ActionContext actionContext = ActionContext.getContext();

        Response response;

        // Action 必须要有缺省构造方法来实例化
        if (!checkDefaultConstructor(type)) {
            response = Response.fail(type.getName() + " 没有缺省构造方法。");

        } else {
            Action action = actionFactory.getAction(type, request);

            if (action != null) {
                actionContext.setAction(action);

                // 执行 Action
                response = execute(request, action);

            } else {
                response = Response.fail("(没有获取到" + type.getName() + "的实例)");
            }
        }

        if (response == null) {
            response = Response.fail("(处理结果为空)");
        }

        return response;
    }

    // 添加本次执行的统计信息
    private void addActionStatistics(ActionContext actionContext) {
        long start = actionContext.getExecutionStartMillis();
        long end = actionContext.getExecutionEndMillis();

        if (start == -1 || end == -1) {
            return;
        }

        this.serverStatistics.addExecutionData(actionContext.getRequest().getFunctionName(), end - start);
    }

    /**
     * 检查指定的类是否有缺省构造方法
     *
     * @param type 要检查的类
     *
     * @return 如果类没有定义构造方法或定义了缺省构造方法，则返回 true
     */
    private boolean checkDefaultConstructor(Class type) {
        if (type.getConstructors().length == 0) {
            return true;
        }

        try {
            Constructor default_cons = type.getConstructor(new Class[]{});
            return Modifier.isPublic(default_cons.getModifiers());
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private Response execute(final Request request, final Action action) {
        Response response;
        try {
            List<String> missingParameters = getMissingParameters(action, request);

            if (!missingParameters.isEmpty()) {
                return Response.fail("参数不能为空：" + missingParameters);
            }

            List<String> mismatchParameters = getMismatchedParameters(action, request);

            if (!mismatchParameters.isEmpty()) {
                return Response.fail("参数格式不正确：" + mismatchParameters);
            }

            // 复制 InterceptorChain 对象，在本线程中使用
            InterceptorChain interceptors = new InterceptorChain(this.interceptors);

            // 调用 InterceptorChain 处理
            response = new ActionInvocation(ActionContext.getContext(), interceptors,

                    new ActionInvocation.FinalInvocation() {
                        @Override
                        public Response invoke() throws Exception {
                            return action.execute(request);
                        }
                    }).invoke();

        } catch (Throwable e) {
            log.error("Action 执行失败", e);
            response = Response.fail(e);
        }

        return response;
    }

    // 检查格式不符的参数
    private List<String> getMismatchedParameters(Action action, Request request) {
        Function function = AnnotationUtils.getFunction(action.getClass());
        if (function == null) {
            return Collections.emptyList();
        }

        Parameter[] parameters = function.parameters();
        List<String> mismatched = new ArrayList<String>();

        for (Parameter parameter : parameters) {
            String pattern = parameter.pattern();
            if (pattern.length() > 0) {
                String name = parameter.name();
                String value = request.getString(name);
                if (!value.matches(pattern)) {
                    mismatched.add(name);
                }
            }
        }

        return mismatched;
    }

    /**
     * 检查请求参数是否满足 Action 类定义的必需参数
     *
     * @param action  Action 对象
     * @param request 请求
     *
     * @return 遗漏的参数
     */
    private List<String> getMissingParameters(Action action, Request request) {
        Function function = AnnotationUtils.getFunction(action.getClass());
        if (function == null) {
            return Collections.emptyList();
        }

        Parameter[] parameters = function.parameters();
        List<String> missingParameters = new ArrayList<String>();

        for (Parameter parameter : parameters) {
            if (parameter.required()) {
                String name = parameter.name();
                if (StringUtils.isEmpty(request.getString(name))) {
                    missingParameters.add(name);
                }
            }
        }

        return missingParameters;
    }

    // 根据类名查找 Action 类
    private Class<Action> findClass(String className) {
        return typeMappings.find(className);
    }

    /////////////////////////////////////////

    List<Class<Action>> actionClasses = new ArrayList<Class<Action>>();

    /**
     * 列出所有的 Action 类
     *
     * @return 所有的 Action 类
     */
    public List<Class<Action>> getActionClasses() {

        if (typeMappings.getPackages() == null) {
            return Collections.emptyList();
        }

        if (actionClasses.isEmpty()) {
            for (String packageName : typeMappings.getPackages()) {
                List<Class<Action>> classes = ClassUtils.findClasses(Action.class, packageName);
                log.debug("found classes from " + packageName + ": " + classes);
                actionClasses.addAll(classes);
            }

            Iterator<Class<Action>> iterator = actionClasses.iterator();
            while (iterator.hasNext()) {
                Class<Action> type = iterator.next();

                // 不列出接口和抽象类
                if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
                    iterator.remove();
                }
            }
        }

        return actionClasses;
    }

    public void shutdown() {
        LogHandlerExecutor.shutdown();
    }

    public void addInterceptor(int position, Interceptor interceptor) {
        this.interceptors.add(position, interceptor);
    }

    public void addInterceptor(Interceptor interceptor) {
        this.interceptors.add(interceptor);
    }
}
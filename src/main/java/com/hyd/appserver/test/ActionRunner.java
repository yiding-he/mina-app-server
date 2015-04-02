package com.hyd.appserver.test;

import com.hyd.appserver.Interceptor;
import com.hyd.appserver.Request;
import com.hyd.appserver.Response;
import com.hyd.appserver.core.ServerConfiguration;
import com.hyd.appserver.core.AppServerCore;
import com.hyd.appserver.core.Protocol;
import com.hyd.appserver.spring.SpringActionRunnerInjector;
import com.hyd.appserver.utils.JsonUtils;

/**
 * 用于在本地运行 Action （用于单元测试）的类
 *
 * @author yiding.he
 */
public class ActionRunner {

    /**
     * 创建 ActionRunner
     *
     * @param packagePath  Action 包路径
     * @param packagePaths Action 包路径
     *
     * @return ActionRunner 对象
     */
    public static ActionRunner create(String packagePath, String... packagePaths) {
        AppServerCore serverCore = new AppServerCore(ServerConfiguration.DEFAULT_CONFIG);
        serverCore.setPackages(concat(packagePath, packagePaths));
        return new ActionRunner(serverCore);
    }

    /**
     * 根据 Spring 配置创建 ActionRunner
     *
     * @param applicationContext Spring 容器（为了让非 Spring 环境下能正常编译，参数类型设为 Object）
     * @param packagePath        Action 包路径
     * @param packagePaths       Action 包路径
     *
     * @return ActionRunner 对象
     */
    public static ActionRunner createFromSpring(Object applicationContext, String packagePath, String... packagePaths) {

        AppServerCore serverCore = new AppServerCore(ServerConfiguration.DEFAULT_CONFIG);
        serverCore.setPackages(concat(packagePath, packagePaths));
        serverCore.setActionFactory(SpringActionRunnerInjector.createActionFactory(applicationContext));

        return new ActionRunner(serverCore);
    }

    private static String[] concat(String str, String[] strs) {
        if (strs == null || strs.length == 0) {
            return new String[]{str};
        }

        String[] result = new String[strs.length + 1];
        result[0] = str;
        System.arraycopy(strs, 0, result, 1, strs.length);
        return result;
    }

    /////////////////////////////////////////

    private AppServerCore core;

    private ActionRunner(AppServerCore core) {
        this.core = core;
    }

    public AppServerCore getCore() {
        return core;
    }

    /**
     * 添加拦截器
     *
     * @param interceptor 拦截器
     */
    public void addInterceptor(Interceptor interceptor) {
        this.core.addInterceptor(interceptor);
    }

    public Response run(Request request) throws Exception {
        Response response = this.core.process(request, Protocol.Json);
        String responseString = JsonUtils.toJson(response);
        return JsonUtils.parseResponse(responseString);
    }
}

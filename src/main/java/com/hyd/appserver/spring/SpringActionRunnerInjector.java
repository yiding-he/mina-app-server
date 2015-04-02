package com.hyd.appserver.spring;

import com.hyd.appserver.core.ActionFactory;
import org.springframework.context.ApplicationContext;

/**
 * 为了不让 ActionRunner 类依赖 Spring，将相关的逻辑移出来
 *
 * @author yiding.he
 */
public class SpringActionRunnerInjector {

    public static ActionFactory createActionFactory(Object o) {
        ApplicationContext context = (ApplicationContext) o;

        // 创建 ActionFactory
        SpringActionFactory actionFactory = new SpringActionFactory();
        actionFactory.setApplicationContext(context);
        return actionFactory;
    }
    
}

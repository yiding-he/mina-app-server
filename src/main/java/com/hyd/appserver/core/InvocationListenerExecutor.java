package com.hyd.appserver.core;

import com.hyd.appserver.ActionContext;
import com.hyd.appserver.InvocationListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 日志处理执行类
 *
 * @author yiding.he
 */
public class InvocationListenerExecutor {

    static volatile ExecutorService service;

    public static final int DEFAULT_MAX_RUNNING = 3;

    private static int maxRunning = DEFAULT_MAX_RUNNING;

    /**
     * 设置日志处理的并发量。本方法必须在服务器运行之前调用。
     *
     * @param maxRunning 日志处理最大并发量
     */
    public static void setMaxRunning(int maxRunning) {
        InvocationListenerExecutor.maxRunning = maxRunning;
    }

    public static void executeHandler(InvocationListener invocationListener, ActionContext context) {
        checkExecutorService();

        service.submit(new LogHandlerRunnable(invocationListener, context));
    }

    private static void checkExecutorService() {
        // Java 5 以上版本中，将要赋值的成员声明为 volatile 就不会存在 DCL 的问题
        // http://jeremymanson.blogspot.com/2008/11/what-volatile-means-in-java.html
        if (service == null) {
            synchronized (InvocationListenerExecutor.class) {
                if (service == null) {
                    service = Executors.newFixedThreadPool(maxRunning);
                }
            }
        }
    }

    public static void shutdown() {
        if (service != null) {
            service.shutdown();
        }
    }

    /////////////////////////////////////////

    private static class LogHandlerRunnable implements Runnable {

        static final Logger log = LogManager.getLogger(InvocationListenerExecutor.class);

        private InvocationListener invocationListener;

        private ActionContext context;

        private LogHandlerRunnable(InvocationListener invocationListener, ActionContext context) {
            this.invocationListener = invocationListener;
            this.context = context;
        }

        // run() 方法在 Executor 线程中执行
        public void run() {
            try {
                invocationListener.invocationFinished(this.context);
            } catch (Exception e) {
                log.error("处理接口日志失败", e);
            }
        }
    }
}

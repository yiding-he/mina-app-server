package com.hyd.appserver.utils;

import org.apache.mina.filter.logging.LogLevel;
import org.apache.mina.filter.logging.LoggingFilter;

/**
 * (description)
 *
 * @author yiding.he
 */
public class MinaUtils {

    public static LoggingFilter createLoggingFilter() {
        LoggingFilter filter = new LoggingFilter("java.io.mina");
        filter.setExceptionCaughtLogLevel(LogLevel.NONE);
        filter.setMessageReceivedLogLevel(LogLevel.DEBUG);
        filter.setMessageSentLogLevel(LogLevel.DEBUG);
        filter.setSessionClosedLogLevel(LogLevel.DEBUG);
        filter.setSessionCreatedLogLevel(LogLevel.DEBUG);
        filter.setSessionIdleLogLevel(LogLevel.DEBUG);
        filter.setSessionOpenedLogLevel(LogLevel.DEBUG);
        return filter;
    }
}

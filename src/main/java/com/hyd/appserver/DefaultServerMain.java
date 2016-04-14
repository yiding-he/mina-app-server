package com.hyd.appserver;

import com.hyd.appserver.core.AppServerFactory;
import com.hyd.appserver.core.ServerConfiguration;
import com.hyd.appserver.utils.Arguments;
import com.hyd.appserver.utils.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 缺省启动类
 *
 * @author yiding.he
 */
public class DefaultServerMain {

    private static List<String> additionalPackages = new ArrayList<String>();

    /**
     * 预先添加额外的 actions 包
     *
     * @param aPackage 额外的 actions 包
     */
    public static void addAdditionalPackage(String aPackage) {
        if (!additionalPackages.contains(aPackage)) {
            additionalPackages.add(aPackage);
        }
    }

    ////////////////////////////////////////////////////////////////

    private static MinaAppServer defaultServer = null;

    public static MinaAppServer getDefaultServer() {
        return defaultServer;
    }

    public static void main(String[] args) throws Exception {

        Arguments arguments = new Arguments(args);
        MinaAppServer server = createDefaultServer(arguments, "/server.properties");
        defaultServer = server;

        // 添加额外的 actions 包
        for (String aPackage : additionalPackages) {
            server.getCore().getTypeMappings().addPackage(aPackage);
        }

        server.start();
    }

    /**
     * 根据配置文件和命令行参数生成 MinaAppServer 对象
     *
     * @param arguments      命令行参数 arguments = new Arguments(args);
     * @param propertiesPath 配置文件路径。如果 arguments 当中存在 -conf 参数，则会被忽略
     *
     * @return 生成的 MinaAppServer 对象
     *
     * @throws IOException
     */
    public static MinaAppServer createDefaultServer(Arguments arguments, String propertiesPath) throws IOException {
        Properties properties = combineArgumentsAndProperties(arguments, propertiesPath);
        return AppServerFactory.createServer(properties);
    }

    public static Properties combineArgumentsAndProperties(Arguments arguments, String propertiesPath) throws IOException {
        if (arguments == null) {
            arguments = new Arguments();
        }

        Properties properties;
        String confPath = arguments.getString("conf");

        if (!StringUtils.isEmpty(confPath)) {
            properties = readPropertyFile(confPath);

        } else {
            InputStream stream = DefaultServerMain.class.getResourceAsStream(propertiesPath);
            if (stream != null) {
                properties = readPropertyStream(stream);
            } else {
                properties = new Properties();
            }
        }

        String ip = arguments.getString("ip");
        if (StringUtils.isEmpty(ip)) {
            ip = properties.getProperty("ip");
        }
        if (StringUtils.isEmpty(ip)) {
            ip = ServerConfiguration.DEFAULT_LISTEN_IP;
        }

        int port = getInteger(arguments, "port", properties,
                "port", String.valueOf(ServerConfiguration.DEFAULT_LISTEN_PORT));
        int adminPort = getInteger(arguments, "admin-port", properties,
                "admin-port", String.valueOf(port + 1000));
        int maxActiveWorkers = getInteger(null, null, properties,
                "max-active-workers", String.valueOf(ServerConfiguration.DEFAULT_MAX_PROCESSORS));
        int idleTimeout = getInteger(null, null, properties,
                "idle-timeout", String.valueOf(ServerConfiguration.DEFAULT_IDLE_WAIT));

        properties.put("ip", ip);
        properties.put("port", port);
        properties.put("admin-port", adminPort);
        properties.put("max-active-workers", maxActiveWorkers);
        properties.put("idle-timeout", idleTimeout);
        return properties;
    }

    private static int getInteger(Arguments arguments, String argKey, Properties properties, String propKey, String defaultValue) {

        if (arguments != null) {
            int argValue = arguments.getInteger(argKey, 0);
            if (argValue != 0) {
                return argValue;
            }
        }

        String value = properties.getProperty(propKey);
        if (value == null || value.trim().length() == 0) {
            value = defaultValue;
        }
        return Integer.parseInt(value);
    }

    /**
     * 从流中读取配置
     *
     * @param stream properties 流
     *
     * @return 读取的配置
     *
     * @throws IOException 如果读取失败
     */
    public static Properties readPropertyStream(InputStream stream) throws IOException {
        Properties properties = new Properties();
        properties.load(stream);
        return properties;
    }

    /**
     * 读取指定的配置文件
     *
     * @param confPath 配置文件路径
     *
     * @return 读取的配置
     *
     * @throws IOException 如果读取失败
     */
    public static Properties readPropertyFile(String confPath) throws IOException {
        FileInputStream stream = new FileInputStream(confPath);
        try {
            return readPropertyStream(stream);
        } finally {
            stream.close();
        }
    }

}

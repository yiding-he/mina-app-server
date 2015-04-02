package demo;

import com.hyd.appserver.core.AppServerFactory;
import com.hyd.appserver.MinaAppServer;

import java.util.Properties;

/**
 * (description)
 *
 * @author yiding.he
 */
public class SpringMinaAppServer {

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.put(AppServerFactory.PROPERTY_ACTION_PACKAGES, "demo.actions");
        properties.put(AppServerFactory.PROPERTY_SPRING_CONFIG, "classpath:application-context.xml");

        MinaAppServer server = AppServerFactory.createServer(properties);
        server.start();
    }
}

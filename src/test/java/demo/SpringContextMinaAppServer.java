package demo;

import com.hyd.appserver.DefaultServerMain;
import com.hyd.appserver.MinaAppServer;
import com.hyd.appserver.spring.SpringAppSeverFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Properties;

/**
 * (description)
 *
 * @author yiding.he
 */
public class SpringContextMinaAppServer {

    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:application-context.xml");

        Properties properties = DefaultServerMain.combineArgumentsAndProperties(null, "/server.properties");

        MinaAppServer server = SpringAppSeverFactory.createServer(properties, applicationContext);
        server.start();
    }
}

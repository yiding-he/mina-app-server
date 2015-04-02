package multipleserver;

import com.hyd.appserver.MinaAppServer;
import com.hyd.appserver.spring.SpringServerInjector;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * todo: description
 *
 * @author yiding.he
 */
public class TestMultipleServerUsingSpring {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("classpath:application-context.xml");

        startServer(context, 8090);
        startServer(context, 8091);
        startServer(context, 8092);
    }

    private static void startServer(ApplicationContext context, int port) {
        MinaAppServer server = new MinaAppServer(port);
        server.setActionPackages("demo.actions");

        SpringServerInjector.init(server, context);
        server.start();
    }
}

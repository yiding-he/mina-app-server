package minimal;

import com.hyd.appserver.MinaAppServer;
import com.hyd.appserver.core.ServerConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("minimal")
public class MinimalServerTest {

    public static void main(String[] args) {

        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MinimalServerTest.class);

        ServerConfiguration serverConfiguration = new ServerConfiguration();
        serverConfiguration.setHttpTestEnabled(true);

        MinaAppServer minaAppServer = new MinaAppServer(serverConfiguration);
        minaAppServer.setApplicationContext(applicationContext);

        minaAppServer.start();
    }
}

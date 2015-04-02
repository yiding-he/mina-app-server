package demo.components;

import com.hyd.appserver.ContextListener;
import com.hyd.appserver.core.ServerConfiguration;
import org.springframework.stereotype.Component;

/**
 * (描述)
 *
 * @author 贺一丁
 */
@Component
public class EmptyListener implements ContextListener {

    @Override
    public void initialize(ServerConfiguration configuration) {
        System.out.println("/////////////////////////////////////// started.");
    }

    @Override
    public void destroy(ServerConfiguration configuration) {
        System.out.println("/////////////////////////////////////// destroyed.");
    }
}

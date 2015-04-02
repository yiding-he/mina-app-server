package demo.components;

import com.hyd.appserver.ContextListener;
import com.hyd.appserver.core.ServerConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * (description)
 * created at 2014/10/8
 *
 * @author Yiding
 */
public class ApplicationContextAwareListener
        implements ContextListener, ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }

    @Override
    public void initialize(ServerConfiguration configuration) {

    }

    @Override
    public void destroy(ServerConfiguration configuration) {

    }
}

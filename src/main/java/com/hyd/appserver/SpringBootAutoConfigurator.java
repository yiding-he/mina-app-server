package com.hyd.appserver;

import com.hyd.appserver.core.ServerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@AutoConfigureOrder()
@ConditionalOnExpression("${mina-app-server.autostart}==true")
@ConditionalOnMissingBean(MinaAppServer.class)
@EnableConfigurationProperties(ServerConfiguration.class)
public class SpringBootAutoConfigurator implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Autowired
    private MinaAppServer minaAppServer;

    @Bean
    public MinaAppServer minaAppServer(ServerConfiguration serverConfiguration) {
        return new MinaAppServer(serverConfiguration);
    }

    @PostConstruct
    public void autoStartMinaAppServer() {
        minaAppServer.setApplicationContext(applicationContext);
        minaAppServer.start();
    }
}

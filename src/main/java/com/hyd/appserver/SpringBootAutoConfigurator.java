package com.hyd.appserver;

import com.hyd.appserver.core.ServerConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@AutoConfigureOrder()
@ConditionalOnProperty("mina-app-server.port")
@ConditionalOnMissingBean(MinaAppServer.class)
@EnableConfigurationProperties(ServerConfiguration.class)
public class SpringBootAutoConfigurator {

    @Bean
    public MinaAppServer minaAppServer(ServerConfiguration serverConfiguration) {
        return new MinaAppServer(serverConfiguration);
    }

    @PostConstruct
    @ConditionalOnExpression("${mina-app-server.autostart}")
    public void autoStartMinaAppServer(MinaAppServer minaAppServer) {
        minaAppServer.start();
    }
}

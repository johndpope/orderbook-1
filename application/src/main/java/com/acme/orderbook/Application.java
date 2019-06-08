package com.acme.orderbook;

import com.acme.orderbook.shutdown.GracefulShutdown;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;

/**
 * Created by robertk on 6/8/2019.
 */
@SpringBootApplication
public class Application {

    @Bean
    public GracefulShutdown gracefulShutdown() {
        return new GracefulShutdown();
    }

    @Bean
    public ConfigurableServletWebServerFactory webServerFactory(final GracefulShutdown gracefulShutdown) {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(gracefulShutdown);

        return factory;
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(Application.class, CoreApplication.class)
                .run(args);
    }
}

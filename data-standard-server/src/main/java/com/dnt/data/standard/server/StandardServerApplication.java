package com.dnt.data.standard.server;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
@Slf4j
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.dnt"})
@EnableAspectJAutoProxy(exposeProxy=true)
@EnableTransactionManagement
@ComponentScans(value = {@ComponentScan(value = "com.dnt")})
public class StandardServerApplication implements CommandLineRunner {


    public static void main(String[] args) {
        SpringApplication.run(StandardServerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("数仓服务启动......");
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> configurer(
            @Value("${spring.application.name}") String applicationName) {
        return (registry) -> registry.
                config().
                commonTags("application", applicationName);
    }


    @Bean
    RestTemplate restTemplate(){
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(1000);
        requestFactory.setReadTimeout(1000);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
    }
}

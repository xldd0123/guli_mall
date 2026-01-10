package com.xueliandan.gulimall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@RestController
public class GuliGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliGatewayApplication.class, args);
    }


    @GetMapping(path = "/hello")
    public String hello() {
        return "hello world";
    }
}

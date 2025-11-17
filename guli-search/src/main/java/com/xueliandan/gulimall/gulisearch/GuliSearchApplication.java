package com.xueliandan.gulimall.gulisearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,})
@EnableDiscoveryClient
@RestController
public class GuliSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliSearchApplication.class, args);
    }

    @GetMapping(path = "/test")
    public String test() {
        return "success!";
    }

}

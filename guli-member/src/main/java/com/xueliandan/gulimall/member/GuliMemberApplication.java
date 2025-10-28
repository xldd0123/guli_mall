package com.xueliandan.gulimall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("com.xueliandan.gulimall.member.dao")
@SpringBootApplication
@EnableFeignClients(basePackages = "com.xueliandan.gulimall.coupon.api.feign")
@EnableDiscoveryClient
public class GuliMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliMemberApplication.class, args);
    }

}

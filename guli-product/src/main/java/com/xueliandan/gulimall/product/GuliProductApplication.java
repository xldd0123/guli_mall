package com.xueliandan.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("com.xueliandan.gulimall.product.dao")
@SpringBootApplication
@EnableFeignClients({"com.xueliandan.gulimall.coupon.api.feign","com.xueliandan.gulimall.ware.api.feign"})
public class GuliProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliProductApplication.class, args);
    }

}

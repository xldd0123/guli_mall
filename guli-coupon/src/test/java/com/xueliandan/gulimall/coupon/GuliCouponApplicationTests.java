package com.xueliandan.gulimall.coupon;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@SpringBootTest
class GuliCouponApplicationTests {

    @Value("${coupon-user.name}")
    private String name;
    @Test
    void contextLoads() {
        System.out.println(name);
    }

}

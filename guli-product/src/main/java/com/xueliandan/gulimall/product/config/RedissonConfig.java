package com.xueliandan.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author zxb 2026/1/22 21:23
 */
@Configuration
public class RedissonConfig {


    /**
     * 所有对 redisson 的操作都通过 Redisson
     * 单节点模式的配置和集群模式的配置不一样
     *
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod = "shutdown")
    RedissonClient redisson() throws IOException {
        // connects to 127.0.0.1:6379 by default
        // RedissonClient redisson = Redisson.create();

        Config config = new Config();
        // use "valkey+uds://" for Valkey Unix Domain Socket (UDS) connection
        // use "valkey://" for Valkey connection
        // use "valkeys://" for Valkey SSL connection
        // use "redis+uds://" for Redis Unix Domain Socket (UDS) connection
        // use "redis://" for Redis connection
        // use "rediss://" for Redis SSL connection
        config.useSingleServer().setAddress("redis://192.168.87.183:6379");
        return Redisson.create(config);
    }
}

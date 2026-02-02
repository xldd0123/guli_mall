package com.xueliandan.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.redisson.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@MapperScan("com.xueliandan.gulimall.product.dao")
@SpringBootApplication
@EnableFeignClients({"com.xueliandan.gulimall.coupon.api.feign",
        "com.xueliandan.gulimall.ware.api.feign",
        "com.xueliandan.gulimall.search.api.feign"})
@EnableCaching
@RestController
public class GuliProductApplication {

    private final Logger log = LoggerFactory.getLogger(GuliProductApplication.class);

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public static void main(String[] args) {
        SpringApplication.run(GuliProductApplication.class, args);
    }


    @GetMapping(path = "/redisson-lock")
    public String testRedissonLock() {
        // 获取【分布式锁】，只要名称相同，就是同一把锁
        RLock lock1 = redissonClient.getLock("lock1");
        lock1.lock(10, TimeUnit.SECONDS);
        try {
            System.out.println("[" + Thread.currentThread().getId() + "]获取到了锁！开始执行业务代码~~~~");
            TimeUnit.SECONDS.sleep(30);
        } catch (Exception e) {

        } finally {
            lock1.unlock();
            System.out.println("[" + Thread.currentThread().getId() + "]释放了锁!");
        }
        return "success!";
    }


    @GetMapping(path = "/read-lock")
    public String readData() {
        RReadWriteLock rReadWriteLock = redissonClient.getReadWriteLock("rw-lock");
        String s;
        RLock rLock = rReadWriteLock.readLock();
        rLock.lock();
        try {
            log.info("读锁加锁成功!");
            TimeUnit.SECONDS.sleep(5);
            s = stringRedisTemplate.opsForValue().get("writeValue");
        } catch (InterruptedException e) {
            return "";
        } finally {
            rLock.unlock();
        }
        return s;
    }


    @GetMapping(path = "/write-lock")
    public String writeData() {
        String string = UUID.randomUUID().toString();
        // 同一把锁
        RReadWriteLock rReadWriteLock = redissonClient.getReadWriteLock("rw-lock");
        RLock writeLock = rReadWriteLock.writeLock();
        writeLock.lock();
        try {
            log.info("写锁加锁成功!");
            TimeUnit.SECONDS.sleep(10);
            stringRedisTemplate.opsForValue().set("writeValue", string);
        } catch (Exception e) {

        } finally {
            writeLock.unlock();
        }
        return string;
    }


    /**
     * 上厕所占坑，坑假设就三个
     *
     * @return 占坑
     */
    @GetMapping(path = "/toToilet")
    public String toToilet() {
        RSemaphore semaphore = redissonClient.getSemaphore("toilet-count");
        boolean isAcquired;
        try {
            // semaphore.acquire(); // 阻塞式
            // 尝试获取，获取不到就拉到
            isAcquired = semaphore.tryAcquire();
            if (isAcquired) {
                log.info("上厕所占坑成功，执行业务。。。");
                return "ok";
            } else {
                return "占坑失败，请等待后重试~~~";
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(path = "/leaveToilet")
    public String leaveToilet() {
        RSemaphore semaphore = redissonClient.getSemaphore("toilet-count");
        semaphore.release();
        log.info("释放厕坑成功!");
        return "ok";
    }

    @GetMapping(path = "/readyGo")
    public String readyGo() {
        RCountDownLatch readyGo = redissonClient.getCountDownLatch("readyGo");
        try {
            // 倒数 3 个数!
            readyGo.trySetCount(3);
            readyGo.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("倒计时结束，冲!");
        return "ok";
    }

    @GetMapping(path = "/timeCountDown")
    public String timeCountDown() {
        RCountDownLatch readyGo = redissonClient.getCountDownLatch("readyGo");
        long count = readyGo.getCount();
        log.info("倒计时 : {}", count);
        readyGo.countDown();
        return "ok";
    }
}

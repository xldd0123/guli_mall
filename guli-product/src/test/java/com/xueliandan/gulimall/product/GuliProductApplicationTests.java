package com.xueliandan.gulimall.product;

import com.xueliandan.gulimall.product.service.PmsBrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

//@Runwith(SpringRunner.class)
@SpringBootTest
class GuliProductApplicationTests {

    @Autowired
    PmsBrandService pmsBrandService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    @Test
    void contextLoads() {
        /*PmsBrandEntity pmsBrand  = new PmsBrandEntity();
        pmsBrand.setName("华为");
        pmsBrandService.save(pmsBrand);

        PmsBrandEntity byId = pmsBrandService.getById(1L);
        System.out.println(byId);*/
//        PmsBrandEntity pmsBrand = new PmsBrandEntity();
//        pmsBrand.setBrandId(1L);
//        pmsBrand.setDescript("华为");
//        pmsBrandService.updateById(pmsBrand);
//
//        PageUtils pageUtils = pmsBrandService.queryPage(new HashMap<>());
//        List<?> list = pageUtils.getList();
//        System.out.println(list);
        boolean b = pmsBrandService.removeById(2L);
        System.out.println("删除是否成功:" + b);
    }

    @Test
    void redisTest() {
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
//        stringStringValueOperations.set("hello", "world");
        System.out.println(stringStringValueOperations.get("hello"));
    }


}

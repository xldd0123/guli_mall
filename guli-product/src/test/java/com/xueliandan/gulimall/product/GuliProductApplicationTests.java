package com.xueliandan.gulimall.product;

import com.xueliandan.gulimall.product.service.PmsBrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

//@Runwith(SpringRunner.class)
@SpringBootTest
class GuliProductApplicationTests {

    @Autowired
    PmsBrandService pmsBrandService;


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


}

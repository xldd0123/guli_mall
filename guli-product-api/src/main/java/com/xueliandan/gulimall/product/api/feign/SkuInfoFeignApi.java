package com.xueliandan.gulimall.product.api.feign;

import com.xueliandan.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zxb 2025/10/21 20:15
 */
@FeignClient(value = "guli-product",contextId = "guli-product-SkuInfoFeignApi")
public interface SkuInfoFeignApi {

    @RequestMapping("/product/sku/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);
}

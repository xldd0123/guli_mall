package com.xueliandan.gulimall.ware.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * @author zxb 2025/11/20 19:58
 */
@FeignClient(value = "guli-ware", contextId = "guli-ware-WareSkuFeignApi")
public interface WareSkuFeignApi {

    @RequestMapping("/rpc/ware/sku/hasStock")
    Map<String, Boolean> skuHasStock(@RequestBody List<Long> skuIds);
}

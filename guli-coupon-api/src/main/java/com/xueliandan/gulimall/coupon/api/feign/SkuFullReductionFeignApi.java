package com.xueliandan.gulimall.coupon.api.feign;

import com.xueliandan.gulimall.common.utils.R;
import com.xueliandan.gulimall.coupon.api.dto.SkuFullReductionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zxb 2025/10/16 14:28
 */
@FeignClient(value = "guli-coupon", contextId = "guli-coupon-SkuFullReductionFeignApi")
public interface SkuFullReductionFeignApi {

    @RequestMapping("/rpc/coupon/skufullreduction/save")
    public R save(@RequestBody SkuFullReductionDTO skuFullReductionDTO);
}

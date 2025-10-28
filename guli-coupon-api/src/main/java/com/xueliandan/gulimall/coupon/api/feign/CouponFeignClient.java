package com.xueliandan.gulimall.coupon.api.feign;

import com.xueliandan.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author zxb 2025/5/1 16:45
 */
@FeignClient(value = "guli-coupon")
public interface CouponFeignClient {

    @GetMapping(path = "/coupon/smscoupon/feign-test")
    public R testFeign();
}

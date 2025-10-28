package com.xueliandan.gulimall.coupon.api.feign;

import com.xueliandan.gulimall.common.utils.R;
import com.xueliandan.gulimall.coupon.api.dto.SkuLadderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author zxb 2025/10/16 13:46
 */
@FeignClient(value = "guli-coupon", contextId = "guli-coupon-SkuLadderFeignApi")
public interface SkuLadderFeignApi {


    @PostMapping("/rpc/coupon/skuladder/save")
    public R save(@RequestBody SkuLadderDTO smsSkuLadder);

}

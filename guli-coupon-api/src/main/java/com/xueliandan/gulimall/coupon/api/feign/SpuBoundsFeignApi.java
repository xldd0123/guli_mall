package com.xueliandan.gulimall.coupon.api.feign;

import com.xueliandan.gulimall.common.utils.R;
import com.xueliandan.gulimall.coupon.api.dto.SpuBoundsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zxb 2025/10/16 16:16
 */
@FeignClient(value = "guli-coupon", contextId = "guli-coupon-SpuBoundsFeignApi")
public interface SpuBoundsFeignApi {

    @RequestMapping("/rpc/coupon/spubounds/save")
    R save(@RequestBody SpuBoundsDTO smsSpuBoundDTO);

}

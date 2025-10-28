package com.xueliandan.gulimall.coupon.api.feign;

import com.xueliandan.gulimall.common.utils.R;
import com.xueliandan.gulimall.coupon.api.dto.SmsMemberPriceDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zxb 2025/10/16 14:34
 */
@FeignClient(value = "guli-coupon", contextId = "guli-coupon-MemberPriceFeignApi")
public interface MemberPriceFeignApi {

    @RequestMapping("/rpc/coupon/memberprice/save")
    R save(@RequestBody SmsMemberPriceDTO smsMemberPriceDTO);

}

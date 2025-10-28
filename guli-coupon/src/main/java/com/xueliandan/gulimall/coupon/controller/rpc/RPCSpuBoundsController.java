package com.xueliandan.gulimall.coupon.controller.rpc;

import com.xueliandan.gulimall.common.utils.R;
import com.xueliandan.gulimall.coupon.api.dto.SpuBoundsDTO;
import com.xueliandan.gulimall.coupon.entity.SmsSpuBoundsEntity;
import com.xueliandan.gulimall.coupon.service.SmsSpuBoundsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zxb 2025/10/16 16:13
 */
@RestController
@RequestMapping("/rpc/coupon/spubounds")
public class RPCSpuBoundsController {

    @Autowired
    private SmsSpuBoundsService smsSpuBoundsService;

    @RequestMapping("/save")
    public R save(@RequestBody SpuBoundsDTO smsSpuBoundDTO) {
        SmsSpuBoundsEntity smsSpuBounds = new SmsSpuBoundsEntity();
        BeanUtils.copyProperties(smsSpuBoundDTO, smsSpuBounds);
        smsSpuBoundsService.save(smsSpuBounds);
        return R.ok();
    }
}

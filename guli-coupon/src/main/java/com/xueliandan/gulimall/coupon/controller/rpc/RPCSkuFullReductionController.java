package com.xueliandan.gulimall.coupon.controller.rpc;

import com.xueliandan.gulimall.common.utils.R;
import com.xueliandan.gulimall.coupon.api.dto.SkuFullReductionDTO;
import com.xueliandan.gulimall.coupon.entity.SmsSkuFullReductionEntity;
import com.xueliandan.gulimall.coupon.service.SmsSkuFullReductionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zxb 2025/10/16 14:22
 */
@RestController
@RequestMapping("/rpc/coupon/skufullreduction")
public class RPCSkuFullReductionController {

    @Autowired
    private SmsSkuFullReductionService smsSkuFullReductionService;

    @RequestMapping("/save")
    public R save(@RequestBody SkuFullReductionDTO skuFullReductionDTO) {
        SmsSkuFullReductionEntity smsSkuFullReduction = new SmsSkuFullReductionEntity();
        BeanUtils.copyProperties(skuFullReductionDTO, smsSkuFullReduction);
        smsSkuFullReductionService.save(smsSkuFullReduction);
        return R.ok();
    }
}

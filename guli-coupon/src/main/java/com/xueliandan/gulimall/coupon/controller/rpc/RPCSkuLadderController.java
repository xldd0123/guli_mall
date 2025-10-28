package com.xueliandan.gulimall.coupon.controller.rpc;

import com.xueliandan.gulimall.common.utils.R;
import com.xueliandan.gulimall.coupon.api.dto.SkuLadderDTO;
import com.xueliandan.gulimall.coupon.entity.SmsSkuLadderEntity;
import com.xueliandan.gulimall.coupon.service.SmsSkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zxb 2025/10/16 13:52
 */
@RestController
@RequestMapping("/rpc/coupon/skuladder")
public class RPCSkuLadderController {

    @Autowired
    private SmsSkuLadderService smsSkuLadderService;

    @PostMapping("/save")
    public R save(@RequestBody SkuLadderDTO smsSkuLadder) {
        SmsSkuLadderEntity smsSkuLadderEntity = new SmsSkuLadderEntity();
        BeanUtils.copyProperties(smsSkuLadder, smsSkuLadderEntity);
        smsSkuLadderService.save(smsSkuLadderEntity);
        return R.ok();
    }
}

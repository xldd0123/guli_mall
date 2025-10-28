package com.xueliandan.gulimall.coupon.controller.rpc;

import com.xueliandan.gulimall.common.utils.R;
import com.xueliandan.gulimall.coupon.api.dto.SmsMemberPriceDTO;
import com.xueliandan.gulimall.coupon.entity.SmsMemberPriceEntity;
import com.xueliandan.gulimall.coupon.service.SmsMemberPriceService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zxb 2025/10/16 14:32
 */
@RestController
@RequestMapping("/rpc/coupon/memberprice")
public class RPCMemberPriceController {

    @Autowired
    private SmsMemberPriceService smsMemberPriceService;

    @RequestMapping("/save")
    public R save(@RequestBody SmsMemberPriceDTO smsMemberPriceDTO) {
        SmsMemberPriceEntity smsMemberPrice = new SmsMemberPriceEntity();
        BeanUtils.copyProperties(smsMemberPriceDTO, smsMemberPrice);
        smsMemberPriceService.save(smsMemberPrice);
        return R.ok();
    }

}

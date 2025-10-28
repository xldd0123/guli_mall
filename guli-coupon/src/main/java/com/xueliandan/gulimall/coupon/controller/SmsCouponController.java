package com.xueliandan.gulimall.coupon.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.xueliandan.gulimall.coupon.api.dto.CouponDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import com.xueliandan.gulimall.coupon.entity.SmsCouponEntity;
import com.xueliandan.gulimall.coupon.service.SmsCouponService;
import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.common.utils.R;


/**
 * 优惠券信息
 *
 * @author zxb
 * @email 1456992938@qq.com
 * @date 2025-04-23 23:11:59
 */
@RefreshScope
@RestController
@RequestMapping("/coupon/smscoupon")
public class SmsCouponController {

    @Autowired
    private SmsCouponService smsCouponService;

    @GetMapping(path = "/feign-test")
    public R testFeign() {
        CouponDTO couponDTO = new CouponDTO();
        couponDTO.setCouponName("满 100 减 10");
        couponDTO.setStartTime(new Date());
        return R.ok().put("coupon", couponDTO).put("code", 200);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("coupon:smscoupon:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = smsCouponService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("coupon:smscoupon:info")
    public R info(@PathVariable("id") Long id) {
        SmsCouponEntity smsCoupon = smsCouponService.getById(id);

        return R.ok().put("smsCoupon", smsCoupon);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("coupon:smscoupon:save")
    public R save(@RequestBody SmsCouponEntity smsCoupon) {
        smsCouponService.save(smsCoupon);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("coupon:smscoupon:update")
    public R update(@RequestBody SmsCouponEntity smsCoupon) {
        smsCouponService.updateById(smsCoupon);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("coupon:smscoupon:delete")
    public R delete(@RequestBody Long[] ids) {
        smsCouponService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}

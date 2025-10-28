package com.xueliandan.gulimall.coupon.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xueliandan.gulimall.coupon.entity.SmsMemberPriceEntity;
import com.xueliandan.gulimall.coupon.service.SmsMemberPriceService;
import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.common.utils.R;



/**
 * 商品会员价格
 *
 * @author zxb
 * @email 1456992938@qq.com
 * @date 2025-04-23 23:11:58
 */
@RestController
@RequestMapping("coupon/memberprice")
public class SmsMemberPriceController {
    @Autowired
    private SmsMemberPriceService smsMemberPriceService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("coupon:smsmemberprice:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = smsMemberPriceService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("coupon:smsmemberprice:info")
    public R info(@PathVariable("id") Long id){
		SmsMemberPriceEntity smsMemberPrice = smsMemberPriceService.getById(id);

        return R.ok().put("smsMemberPrice", smsMemberPrice);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("coupon:smsmemberprice:save")
    public R save(@RequestBody SmsMemberPriceEntity smsMemberPrice){
		smsMemberPriceService.save(smsMemberPrice);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("coupon:smsmemberprice:update")
    public R update(@RequestBody SmsMemberPriceEntity smsMemberPrice){
		smsMemberPriceService.updateById(smsMemberPrice);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("coupon:smsmemberprice:delete")
    public R delete(@RequestBody Long[] ids){
		smsMemberPriceService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}

package com.xueliandan.gulimall.product.controller;

import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.common.utils.R;
import com.xueliandan.gulimall.product.entity.PmsSkuInfoEntity;
import com.xueliandan.gulimall.product.entity.vo.SkuVO;
import com.xueliandan.gulimall.product.service.PmsSkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * sku信息
 *
 * @author zxb
 * @email 1456992938@qq.com
 * @date 2025-04-23 23:50:22
 */
@RestController
@RequestMapping("/product/skuinfo")
public class PmsSkuInfoController {
    @Autowired
    private PmsSkuInfoService pmsSkuInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:pmsskuinfo:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = pmsSkuInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{skuId}")
    //@RequiresPermissions("product:pmsskuinfo:info")
    public R info(@PathVariable("skuId") Long skuId) {
        PmsSkuInfoEntity pmsSkuInfo = pmsSkuInfoService.getById(skuId);

        return R.ok().put("pmsSkuInfo", pmsSkuInfo);
    }

    /**
     * 保存 SKU 信息
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:pmsskuinfo:save")
    public R save(@RequestBody PmsSkuInfoEntity pmsSkuInfo) {
        pmsSkuInfoService.save(pmsSkuInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:pmsskuinfo:update")
    public R update(@RequestBody PmsSkuInfoEntity pmsSkuInfo) {
        pmsSkuInfoService.updateById(pmsSkuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:pmsskuinfo:delete")
    public R delete(@RequestBody Long[] skuIds) {
        pmsSkuInfoService.removeByIds(Arrays.asList(skuIds));

        return R.ok();
    }

}

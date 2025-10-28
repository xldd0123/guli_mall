package com.xueliandan.gulimall.product.controller;

import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.common.utils.R;
import com.xueliandan.gulimall.common.validator.group.AddGroup;
import com.xueliandan.gulimall.common.validator.group.UpdateGroup;
import com.xueliandan.gulimall.product.dao.PmsCategoryBrandRelationDao;
import com.xueliandan.gulimall.product.entity.PmsBrandEntity;
import com.xueliandan.gulimall.product.service.PmsBrandService;
import com.xueliandan.gulimall.product.service.PmsCategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 品牌
 *
 * @author zxb
 * @email 1456992938@qq.com
 * @date 2025-04-23 23:50:23
 */
@RestController
@RequestMapping("product/brand")
public class PmsBrandController {
    @Autowired
    private PmsBrandService pmsBrandService;
    @Autowired
    private PmsCategoryBrandRelationDao pmsCategoryBrandRelationDao;

    @Autowired
    private PmsCategoryBrandRelationService pmsCategoryBrandRelationService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:pmsbrand:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = pmsBrandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{brandId}")
    //@RequiresPermissions("product:pmsbrand:info")
    public R info(@PathVariable("brandId") Long brandId) {
        PmsBrandEntity pmsBrand = pmsBrandService.getById(brandId);

        return R.ok().put("pmsBrand", pmsBrand);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("product:pmsbrand:save")
    public R save(@RequestBody @Validated(value = {AddGroup.class}) PmsBrandEntity pmsBrand) {
        pmsBrandService.save(pmsBrand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:pmsbrand:update")
    public R update(@RequestBody @Validated(value = {UpdateGroup.class}) PmsBrandEntity pmsBrand) {
        pmsBrandService.updateById(pmsBrand);
        // 同步修改品牌关联分类的名称
        pmsCategoryBrandRelationService.updateBrandName(pmsBrand.getBrandId(), pmsBrand.getName());
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:pmsbrand:delete")
    public R delete(@RequestBody Long[] brandIds) {
        pmsBrandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}

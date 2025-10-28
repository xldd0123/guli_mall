package com.xueliandan.gulimall.product.controller;

import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.common.utils.R;
import com.xueliandan.gulimall.product.entity.PmsCategoryBrandRelationEntity;
import com.xueliandan.gulimall.product.entity.vo.BrandVO;
import com.xueliandan.gulimall.product.service.PmsCategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 品牌分类关联
 *
 * @author zxb
 * @email 1456992938@qq.com
 * @date 2025-04-23 23:50:22
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class PmsCategoryBrandRelationController {
    @Autowired
    private PmsCategoryBrandRelationService pmsCategoryBrandRelationService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:pmscategorybrandrelation:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = pmsCategoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 查询分类关联的品牌
     */
    @RequestMapping("/brands/list")
    //@RequiresPermissions("product:pmscategorybrandrelation:list")
    public R brandRelationList(@RequestParam(name = "catId", required = false) String catId) {
        List<BrandVO> relations = pmsCategoryBrandRelationService.brandRelations(catId);
        return R.ok().put("data", relations);
    }


    @RequestMapping("/catelog/list")
    //@RequiresPermissions("product:pmscategorybrandrelation:list")
    public R catelogList(@RequestParam(name = "brandId") Long brandId) {

        List<PmsCategoryBrandRelationEntity> relateBrands = pmsCategoryBrandRelationService.query()
                .eq("brand_id", brandId).list();
        return R.ok().put("data", relateBrands);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:pmscategorybrandrelation:info")
    public R info(@PathVariable("id") Long id) {
        PmsCategoryBrandRelationEntity pmsCategoryBrandRelation = pmsCategoryBrandRelationService.getById(id);

        return R.ok().put("pmsCategoryBrandRelation", pmsCategoryBrandRelation);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("product:pmscategorybrandrelation:save")
    public R save(@RequestBody PmsCategoryBrandRelationEntity pmsCategoryBrandRelation) {
//        pmsCategoryBrandRelationService.save(pmsCategoryBrandRelation);
        pmsCategoryBrandRelationService.saveWithName(pmsCategoryBrandRelation);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:pmscategorybrandrelation:update")
    public R update(@RequestBody PmsCategoryBrandRelationEntity pmsCategoryBrandRelation) {
        pmsCategoryBrandRelationService.updateById(pmsCategoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:pmscategorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids) {
        pmsCategoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}

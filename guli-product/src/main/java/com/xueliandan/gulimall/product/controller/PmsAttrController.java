package com.xueliandan.gulimall.product.controller;

import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.common.utils.R;
import com.xueliandan.gulimall.product.entity.PmsProductAttrValueEntity;
import com.xueliandan.gulimall.product.entity.vo.AttrVO;
import com.xueliandan.gulimall.product.service.PmsAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 商品属性
 *
 * @author zxb
 * @email 1456992938@qq.com
 * @date 2025-04-23 23:50:23
 */
@RestController
@RequestMapping("/product/attr")
public class PmsAttrController {
    @Autowired
    private PmsAttrService pmsAttrService;


    /**
     * 在商品维护-spu管理列表中，操作一列单击规格，会查出 spu 的所有规格属性，用于再次修改
     * pms_product_attr_value 表
     */
    @GetMapping(path = "/base/listforspu/{spuId}")
    public R attrGroupList(@PathVariable(name = "spuId") Long spuId) {
        List<PmsProductAttrValueEntity> dataList = pmsAttrService.getBaseAttrsBySpuId(spuId);
        return R.ok().put("data", dataList);
    }

    @PostMapping(path = "/update/{spuId}")
    public R productAttrUpdate(@PathVariable(name = "spuId") Long spuId,
                               @RequestBody List<PmsProductAttrValueEntity> productAttrValues) {
        pmsAttrService.productAttrUpdate(spuId, productAttrValues);
        return R.ok("更新成功!");
    }

    /**
     * 平台属性-规格参数列表
     *
     * @param params
     * @param categoryId
     * @return
     */
    @RequestMapping("/base/list/{categoryId}")
    //@RequiresPermissions("product:pmsattrgroup:list")
    public R basePageList(@RequestParam Map<String, Object> params,
                          @PathVariable(name = "categoryId") Long categoryId) {
        PageUtils page = pmsAttrService.basePageList(params, categoryId);
        return R.ok().put("page", page);
    }

    /**
     * 平台属性-销售属性列表
     *
     * @param params
     * @param categoryId
     * @return
     */
    @RequestMapping("/sale/list/{categoryId}")
    public R salePageList(@RequestParam Map<String, Object> params,
                          @PathVariable(name = "categoryId") Long categoryId) {
        PageUtils page = pmsAttrService.salePageList(params, categoryId);
        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:pmsattr:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = pmsAttrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:pmsattr:info")
    public R info(@PathVariable("attrId") Long attrId) {

        AttrVO attrVO = pmsAttrService.getInfoById(attrId);
        return R.ok().put("attr", attrVO);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:pmsattr:save")
    public R save(@RequestBody AttrVO AttrVO) {
//		pmsAttrService.save(pmsAttr);
        pmsAttrService.saveWithRelation(AttrVO);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:pmsattr:update")
    public R update(@RequestBody AttrVO pmsAttr) {
        pmsAttrService.updateInfo(pmsAttr);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:pmsattr:delete")
    public R delete(@RequestBody Long[] attrIds) {
        pmsAttrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}

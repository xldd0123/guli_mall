package com.xueliandan.gulimall.product.controller;

import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.common.utils.R;
import com.xueliandan.gulimall.product.entity.PmsSpuInfoEntity;
import com.xueliandan.gulimall.product.entity.vo.SpuSaveVo;
import com.xueliandan.gulimall.product.service.PmsSpuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * spu信息
 *
 * @author zxb
 * @email 1456992938@qq.com
 * @date 2025-04-23 23:50:21
 */
@RestController
@RequestMapping("product/spuinfo")
public class PmsSpuInfoController {
    @Autowired
    private PmsSpuInfoService pmsSpuInfoService;


    /**
     * SPU 上架功能。
     * 只有上架了的 SPU 才能被全文检索
     */
    @PostMapping(path = "/{spuId}/up")
    public R spuUp(@PathVariable(name = "spuId") Long spuId) {
        pmsSpuInfoService.spuUp(spuId);
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:pmsspuinfo:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = pmsSpuInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:pmsspuinfo:info")
    public R info(@PathVariable("id") Long id) {
        PmsSpuInfoEntity pmsSpuInfo = pmsSpuInfoService.getById(id);

        return R.ok().put("pmsSpuInfo", pmsSpuInfo);
    }

    /**
     * 保存 SPU 商品
     */
    @PostMapping("/save")
    //@RequiresPermissions("product:pmsspuinfo:save")
    public R save(@RequestBody SpuSaveVo spuSaveVo) {
//		pmsSpuInfoService.save(pmsSpuInfo);
        pmsSpuInfoService.saveSpuInfoVO(spuSaveVo);
        return R.ok("保存商品成功!");
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:pmsspuinfo:update")
    public R update(@RequestBody PmsSpuInfoEntity pmsSpuInfo) {
        pmsSpuInfoService.updateById(pmsSpuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:pmsspuinfo:delete")
    public R delete(@RequestBody Long[] ids) {
        pmsSpuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}

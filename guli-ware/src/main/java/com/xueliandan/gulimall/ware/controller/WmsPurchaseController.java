package com.xueliandan.gulimall.ware.controller;

import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.common.utils.R;
import com.xueliandan.gulimall.ware.entity.WmsPurchaseEntity;
import com.xueliandan.gulimall.ware.entity.vo.MergeVO;
import com.xueliandan.gulimall.ware.entity.vo.PurchaseFinishedVO;
import com.xueliandan.gulimall.ware.service.WmsPurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 采购信息
 *
 * @author zxb
 * @email 1456992938@qq.com
 * @date 2025-04-23 23:52:57
 */
@RestController
@RequestMapping("/ware/purchase")
public class WmsPurchaseController {
    @Autowired
    private WmsPurchaseService wmsPurchaseService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:wmspurchase:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = wmsPurchaseService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 获取未被领取的采购单
     */
    @RequestMapping("/unreceive/list")
    public R unReceiveList(@RequestParam Map<String, Object> params) {
        PageUtils page = wmsPurchaseService.listUnReceivePurchase(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:wmspurchase:info")
    public R info(@PathVariable("id") Long id) {
        WmsPurchaseEntity wmsPurchase = wmsPurchaseService.getById(id);

        return R.ok().put("wmsPurchase", wmsPurchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:wmspurchase:save")
    public R save(@RequestBody WmsPurchaseEntity wmsPurchase) {
        wmsPurchaseService.save(wmsPurchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:wmspurchase:update")
    public R update(@RequestBody WmsPurchaseEntity wmsPurchase) {
        wmsPurchaseService.updateById(wmsPurchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:wmspurchase:delete")
    public R delete(@RequestBody Long[] ids) {
        wmsPurchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }


    /**
     * 采购需求合并到采购单
     */
    @PostMapping("/merge")
    public R mergePurchase(@RequestBody MergeVO mergeVO) {
        wmsPurchaseService.mergePurchase(mergeVO);
        return R.ok();
    }

    /**
     * 采购人员领取采购单
     *
     * @param ids 采购单 id 集合
     */
    @PostMapping("/received")
    public R received(@RequestBody List<Long> ids) {
        wmsPurchaseService.received(ids);
        return R.ok();
    }


    /**
     * 完成采购
     */
    @PostMapping("/done")
    public R finishPurchase(@RequestBody PurchaseFinishedVO purchaseFinishedVO) {
        wmsPurchaseService.finishPurchase(purchaseFinishedVO);
        return R.ok();
    }

}

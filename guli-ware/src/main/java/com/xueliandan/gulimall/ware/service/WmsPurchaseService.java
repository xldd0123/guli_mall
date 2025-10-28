package com.xueliandan.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.ware.entity.WmsPurchaseEntity;
import com.xueliandan.gulimall.ware.entity.vo.MergeVO;
import com.xueliandan.gulimall.ware.entity.vo.PurchaseFinishedVO;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author zxb
 * @email 1456992938@qq.com
 * @date 2025-04-23 23:52:57
 */
public interface WmsPurchaseService extends IService<WmsPurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils listUnReceivePurchase(Map<String, Object> params);

    void mergePurchase(MergeVO mergeVO);

    void received(List<Long> ids);

    void finishPurchase(PurchaseFinishedVO purchaseFinishedVO);
}


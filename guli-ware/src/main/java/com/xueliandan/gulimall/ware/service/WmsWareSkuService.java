package com.xueliandan.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.ware.entity.WmsWareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author zxb
 * @email 1456992938@qq.com
 * @date 2025-04-23 23:52:57
 */
public interface WmsWareSkuService extends IService<WmsWareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long wareId, Long skuId, Integer skuNum);

    Map<String, Boolean> skusHasStock(List<Long> skuIds);
}


package com.xueliandan.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.product.entity.PmsSkuInfoEntity;
import com.xueliandan.gulimall.product.entity.vo.SkuVO;

import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author zxb
 * @email 1456992938@qq.com
 * @date 2025-04-23 23:50:22
 */
public interface PmsSkuInfoService extends IService<PmsSkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<PmsSkuInfoEntity> selectBySpuId(Long spuId);
}


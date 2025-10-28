package com.xueliandan.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.product.entity.PmsAttrEntity;
import com.xueliandan.gulimall.product.entity.PmsProductAttrValueEntity;
import com.xueliandan.gulimall.product.entity.vo.AttrVO;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author zxb
 * @email 1456992938@qq.com
 * @date 2025-04-23 23:50:23
 */
public interface PmsAttrService extends IService<PmsAttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveWithRelation(AttrVO attrVO);

    PageUtils basePageList(Map<String, Object> params, Long categoryId);

    AttrVO getInfoById(Long attrId);

    void updateInfo(AttrVO pmsAttr);

    PageUtils salePageList(Map<String, Object> params, Long categoryId);

    List<PmsProductAttrValueEntity> getBaseAttrsBySpuId(Long spuId);

    void productAttrUpdate(Long spuId, List<PmsProductAttrValueEntity> productAttrValues);
}


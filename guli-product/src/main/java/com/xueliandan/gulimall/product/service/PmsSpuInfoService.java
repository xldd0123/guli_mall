package com.xueliandan.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.product.entity.PmsProductAttrValueEntity;
import com.xueliandan.gulimall.product.entity.PmsSpuImagesEntity;
import com.xueliandan.gulimall.product.entity.PmsSpuInfoDescEntity;
import com.xueliandan.gulimall.product.entity.PmsSpuInfoEntity;
import com.xueliandan.gulimall.product.entity.vo.SpuSaveVo;

import java.util.List;
import java.util.Map;

/**
 * spu信息
 *
 * @author zxb
 * @email 1456992938@qq.com
 * @date 2025-04-23 23:50:21
 */
public interface PmsSpuInfoService extends IService<PmsSpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfoVO(SpuSaveVo spuSaveVo);

    void saveSpuInfo(PmsSpuInfoEntity infoEntity);

    void saveSpuDescInfo(PmsSpuInfoDescEntity spuInfoDescEntity);

    void saveSpuImages(List<PmsSpuImagesEntity> pmsSpuImagesEntities);

    void saveSpuBaseAttrs(List<PmsProductAttrValueEntity> pmsProductAttrValueEntities);

    void spuUp(Long spuId);
}


package com.xueliandan.gulimall.product.dao;

import com.xueliandan.gulimall.product.entity.PmsSpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author zxb
 * @email 1456992938@qq.com
 * @date 2025-04-23 23:50:21
 */
@Mapper
public interface PmsSpuInfoDao extends BaseMapper<PmsSpuInfoEntity> {

    void updateSpuPublishStatus(@Param("spuId") Long spuId, @Param("publishStatus") int publishStatus);
}

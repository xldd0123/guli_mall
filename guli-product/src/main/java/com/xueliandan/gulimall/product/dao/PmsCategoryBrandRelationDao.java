package com.xueliandan.gulimall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xueliandan.gulimall.product.entity.PmsCategoryBrandRelationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 品牌分类关联
 *
 * @author zxb
 * @email 1456992938@qq.com
 * @date 2025-04-23 23:50:22
 */
@Mapper
public interface PmsCategoryBrandRelationDao extends BaseMapper<PmsCategoryBrandRelationEntity> {

    void updateCategoryName(@Param("catId") Long catId, @Param("name") String name);

}

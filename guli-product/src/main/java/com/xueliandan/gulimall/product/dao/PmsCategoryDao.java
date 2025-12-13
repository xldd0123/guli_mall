package com.xueliandan.gulimall.product.dao;

import com.xueliandan.gulimall.product.entity.PmsCategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 商品三级分类
 * 
 * @author zxb
 * @email 1456992938@qq.com
 * @date 2025-04-23 23:50:22
 */
@Mapper
public interface PmsCategoryDao extends BaseMapper<PmsCategoryEntity> {

    List<PmsCategoryEntity> findAllCatelogByParentCatIdIn(@Param("catIds") Collection<Long> catIds);
}

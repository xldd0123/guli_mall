package com.xueliandan.gulimall.product.dao;

import com.xueliandan.gulimall.product.entity.PmsAttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 *
 * @author zxb
 * @email 1456992938@qq.com
 * @date 2025-04-23 23:50:23
 */
@Mapper
public interface PmsAttrAttrgroupRelationDao extends BaseMapper<PmsAttrAttrgroupRelationEntity> {

    // 先一对一来做吧
    PmsAttrAttrgroupRelationEntity selectByAttrId(@Param("attrId") Long attrId);

    List<PmsAttrAttrgroupRelationEntity> selectByAttrGroupId(@Param("attrGroupId") Long attrGroupId);

    void deleteByAttrGroupIdAndAttrId(@Param("attrGroupId") Long attrGroupId, @Param("attrId") Long attrId);
}

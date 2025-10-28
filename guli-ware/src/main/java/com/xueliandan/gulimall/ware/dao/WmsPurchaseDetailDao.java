package com.xueliandan.gulimall.ware.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xueliandan.gulimall.ware.entity.WmsPurchaseDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zxb
 * @email 1456992938@qq.com
 * @date 2025-04-23 23:52:57
 */
@Mapper
public interface WmsPurchaseDetailDao extends BaseMapper<WmsPurchaseDetailEntity> {

    List<WmsPurchaseDetailEntity> selectByPurchaseIds(@Param("ids") List<Long> ids);
}

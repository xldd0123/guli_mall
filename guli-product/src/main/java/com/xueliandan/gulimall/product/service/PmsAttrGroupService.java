package com.xueliandan.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.product.entity.PmsAttrEntity;
import com.xueliandan.gulimall.product.entity.PmsAttrGroupEntity;
import com.xueliandan.gulimall.product.entity.query.AttrQueryWrapper;
import com.xueliandan.gulimall.product.entity.vo.AttrAttrgroupRelationVO;
import com.xueliandan.gulimall.product.entity.vo.AttrVO;
import com.xueliandan.gulimall.product.entity.vo.GroupAttrVO;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author zxb
 * @email 1456992938@qq.com
 * @date 2025-04-23 23:50:23
 */
public interface PmsAttrGroupService extends IService<PmsAttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long categoryId);

    List<AttrAttrgroupRelationVO> pageGroupAttrs(Long groupId);

    void deleteGroupRelation(List<AttrQueryWrapper> relations);

    PageUtils groupNoRelation(Map<String, Object> params, Long attrGroupId);

    void batchSaveGroupRelation(List<AttrQueryWrapper> relations);

    List<GroupAttrVO> categoryGroupWithAttr(Long categoryId);
}


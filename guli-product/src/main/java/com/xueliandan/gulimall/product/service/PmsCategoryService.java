package com.xueliandan.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.product.entity.PmsAttrGroupEntity;
import com.xueliandan.gulimall.product.entity.PmsCategoryEntity;

import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * 商品三级分类
 *
 * @author zxb
 * @email 1456992938@qq.com
 * @date 2025-04-23 23:50:22
 */
public interface PmsCategoryService extends IService<PmsCategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 返回树结构分类
     *
     * @return 树结构分类
     */
    List<PmsCategoryEntity> listTree();

    void enrichCategoryPath(PmsAttrGroupEntity pmsAttrGroup);

    void doEnrichCategoryPath(Long categoryId, Stack<Long> stack);
}


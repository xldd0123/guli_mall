package com.xueliandan.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.common.validator.group.AddGroup;
import com.xueliandan.gulimall.common.validator.group.UpdateGroup;
import com.xueliandan.gulimall.product.entity.PmsCategoryBrandRelationEntity;
import com.xueliandan.gulimall.product.entity.vo.BrandVO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author zxb
 * @email 1456992938@qq.com
 * @date 2025-04-23 23:50:22
 */
public interface PmsCategoryBrandRelationService extends IService<PmsCategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveWithName(PmsCategoryBrandRelationEntity pmsCategoryBrandRelation);

    void updateBrandName(@Null(message = "新增时品牌 id 必须为空", groups = {AddGroup.class}) @NotNull(message = "修改时品牌 id 必须不为空", groups = {UpdateGroup.class}) Long brandId, @NotBlank(message = "品牌名 不能为空", groups = {AddGroup.class, UpdateGroup.class}) String name);

    List<BrandVO> brandRelations(String catId);
}


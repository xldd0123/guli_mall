package com.xueliandan.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.common.utils.Query;
import com.xueliandan.gulimall.product.dao.PmsBrandDao;
import com.xueliandan.gulimall.product.dao.PmsCategoryBrandRelationDao;
import com.xueliandan.gulimall.product.dao.PmsCategoryDao;
import com.xueliandan.gulimall.product.entity.PmsBrandEntity;
import com.xueliandan.gulimall.product.entity.PmsCategoryBrandRelationEntity;
import com.xueliandan.gulimall.product.entity.PmsCategoryEntity;
import com.xueliandan.gulimall.product.entity.vo.BrandVO;
import com.xueliandan.gulimall.product.service.PmsCategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@Service("pmsCategoryBrandRelationService")
public class PmsCategoryBrandRelationServiceImpl extends ServiceImpl<PmsCategoryBrandRelationDao, PmsCategoryBrandRelationEntity> implements PmsCategoryBrandRelationService {

    @Autowired
    private PmsCategoryDao pmsCategoryDao;

    @Autowired
    private PmsBrandDao pmsBrandDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PmsCategoryBrandRelationEntity> page = this.page(
                new Query<PmsCategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<PmsCategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveWithName(PmsCategoryBrandRelationEntity pmsCategoryBrandRelation) {
        Long brandId = pmsCategoryBrandRelation.getBrandId();
        Long catelogId = pmsCategoryBrandRelation.getCatelogId();

        PmsCategoryEntity pmsCategoryEntity = pmsCategoryDao.selectById(catelogId);
        pmsCategoryBrandRelation.setCatelogName(pmsCategoryEntity.getName());

        PmsBrandEntity pmsBrand = pmsBrandDao.selectById(brandId);
        pmsCategoryBrandRelation.setBrandName(pmsBrand.getName());

        this.save(pmsCategoryBrandRelation);

    }

    @Override
    public void updateBrandName(Long brandId, String name) {
        this.update(new UpdateWrapper<PmsCategoryBrandRelationEntity>().eq("brand_id", brandId).set("brand_name", name));
    }

    @Override
    public List<BrandVO> brandRelations(String catId) {
        if (null == catId) return Collections.emptyList();
        List<PmsCategoryBrandRelationEntity> catIdRelations = this.list(new QueryWrapper<PmsCategoryBrandRelationEntity>().eq("catelog_id", catId));
        List<BrandVO> retVal = new ArrayList<>();
        if (!CollectionUtils.isEmpty(catIdRelations)) {
            catIdRelations.forEach(relation -> {
                BrandVO relationVO = new BrandVO();
                relationVO.setBrandId(relation.getBrandId());
                relationVO.setBrandName(relation.getBrandName());
                retVal.add(relationVO);
            });
        }
        return retVal;
    }

}
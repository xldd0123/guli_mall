package com.xueliandan.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.common.utils.Query;
import com.xueliandan.gulimall.product.dao.*;
import com.xueliandan.gulimall.product.entity.*;
import com.xueliandan.gulimall.product.entity.vo.AttrVO;
import com.xueliandan.gulimall.product.service.PmsAttrService;
import com.xueliandan.gulimall.product.service.PmsCategoryService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service("pmsAttrService")
public class PmsAttrServiceImpl extends ServiceImpl<PmsAttrDao, PmsAttrEntity> implements PmsAttrService {

    @Autowired
    private PmsAttrAttrgroupRelationDao pmsAttrAttrgroupRelationDao;
    @Autowired
    private PmsAttrGroupDao pmsAttrGroupDao;
    @Autowired
    private PmsCategoryDao pmsCategoryDao;
    @Autowired
    private PmsCategoryService pmsCategoryService;
    @Autowired
    private PmsProductAttrValueDao pmsProductAttrValueDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PmsAttrEntity> page = this.page(
                new Query<PmsAttrEntity>().getPage(params),
                new QueryWrapper<PmsAttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveWithRelation(AttrVO attrVO) {
        PmsAttrEntity pmsAttrPO = new PmsAttrEntity();
        BeanUtils.copyProperties(attrVO, pmsAttrPO);
        this.save(pmsAttrPO);

        Long attrGroupId = attrVO.getAttrGroupId();
        Long attrId = pmsAttrPO.getAttrId();

        if (Objects.nonNull(attrGroupId)) {
            PmsAttrAttrgroupRelationEntity pmsAttrAttrgroupRelationPO = new PmsAttrAttrgroupRelationEntity();
            pmsAttrAttrgroupRelationPO.setAttrGroupId(attrGroupId);
            pmsAttrAttrgroupRelationPO.setAttrId(attrId);
            pmsAttrAttrgroupRelationDao.insert(pmsAttrAttrgroupRelationPO);
        }
    }

    @Override
    public PageUtils basePageList(Map<String, Object> params, Long categoryId) {
        QueryWrapper<PmsAttrEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_type", 1);
        if (0L != categoryId) {
            queryWrapper.eq("catelog_id", categoryId);
        }
        String key = (String) params.get("key");
        // attr_id, attr_name
        if (StringUtils.isNotEmpty(key)) {
            queryWrapper.and((q) ->
                    q.eq("attr_id", key).or().like("attr_name", key));
        }
        IPage<PmsAttrEntity> page = this.page(
                new Query<PmsAttrEntity>().getPage(params),
                queryWrapper
        );

        // 查出关联属性
        // 所属分类、所属分组
        List<PmsAttrEntity> records = page.getRecords();
        List<AttrVO> attrVOS = records.stream().map(record -> {
            AttrVO attrVO = new AttrVO();
            BeanUtils.copyProperties(record, attrVO);
            Long catelogId = record.getCatelogId();
            PmsCategoryEntity pmsCategoryEntity = pmsCategoryDao.selectById(catelogId);
            attrVO.setCatelogName(pmsCategoryEntity.getName());

            Long attrId = record.getAttrId();
            PmsAttrAttrgroupRelationEntity relation = pmsAttrAttrgroupRelationDao.selectByAttrId(attrId);
            if (Objects.nonNull(relation)) {
                Long attrGroupId = relation.getAttrGroupId();
                PmsAttrGroupEntity pmsAttrGroupEntity = pmsAttrGroupDao.selectById(attrGroupId);
                attrVO.setAttrId(record.getAttrId());
                attrVO.setAttrGroupName(pmsAttrGroupEntity.getAttrGroupName());
            }
            return attrVO;
        }).collect(Collectors.toList());

        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(attrVOS);
        return pageUtils;
    }

    @Override
    public PageUtils salePageList(Map<String, Object> params, Long categoryId) {
        QueryWrapper<PmsAttrEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_type", 0);
        if (0L != categoryId) {
            queryWrapper.eq("catelog_id", categoryId);
        }
        String key = (String) params.get("key");
        // attr_id, attr_name
        if (StringUtils.isNotEmpty(key)) {
            queryWrapper.and((q) ->
                    q.eq("attr_id", key).or().like("attr_name", key));
        }
        IPage<PmsAttrEntity> page = this.page(
                new Query<PmsAttrEntity>().getPage(params),
                queryWrapper
        );

        List<PmsAttrEntity> records = page.getRecords();

        List<AttrVO> attrVOS = records.stream().map(record -> {
            AttrVO attrVO = new AttrVO();
            BeanUtils.copyProperties(record, attrVO);
            Long catelogId = record.getCatelogId();
            PmsCategoryEntity pmsCategoryEntity = pmsCategoryDao.selectById(catelogId);
            attrVO.setCatelogName(pmsCategoryEntity.getName());
            return attrVO;
        }).collect(Collectors.toList());

        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(attrVOS);
        return pageUtils;
    }

    @Override
    public List<PmsProductAttrValueEntity> getBaseAttrsBySpuId(Long spuId) {

        QueryWrapper<PmsProductAttrValueEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("spu_id", spuId);

        return pmsProductAttrValueDao.selectList(queryWrapper);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void productAttrUpdate(Long spuId, List<PmsProductAttrValueEntity> productAttrValues) {
        if (CollectionUtils.isNotEmpty(productAttrValues)) {
            List<PmsProductAttrValueEntity> productAttrValueEntities = pmsProductAttrValueDao.selectList(new QueryWrapper<PmsProductAttrValueEntity>().eq("spu_id", spuId));
            if (CollectionUtils.isNotEmpty(productAttrValueEntities)) {
                Map<Long, PmsProductAttrValueEntity> attrIdAndValueEntityMap =
                        productAttrValueEntities.stream().collect(Collectors.toMap(PmsProductAttrValueEntity::getAttrId, Function.identity()));
                List<PmsProductAttrValueEntity> toUpdate = new ArrayList<>();
                productAttrValues.forEach(productAttrValue -> {
                    Long attrId = productAttrValue.getAttrId();
                    if (Objects.nonNull(attrId) && attrIdAndValueEntityMap.containsKey(attrId)) {
                        PmsProductAttrValueEntity pmsProductAttrValueEntity = attrIdAndValueEntityMap.get(attrId);
                        pmsProductAttrValueEntity.setAttrValue(productAttrValue.getAttrValue());
                        toUpdate.add(pmsProductAttrValueEntity);
                    }
                });
                if (CollectionUtils.isNotEmpty(toUpdate)) {
                    pmsProductAttrValueDao.updateById(toUpdate);
                }
            } else {
                Set<PmsProductAttrValueEntity> toSaved = productAttrValues.stream().map(productAttrValue -> {
                    productAttrValue.setSpuId(spuId);
                    return productAttrValue;
                }).collect(Collectors.toSet());
                pmsProductAttrValueDao.insert(toSaved);
            }
        }
    }

    @Override
    public AttrVO getInfoById(Long attrId) {
        PmsAttrEntity pmsAttr = this.getById(attrId);
        Long catelogId = pmsAttr.getCatelogId();


        Stack<Long> stack = new Stack<>();
        pmsCategoryService.doEnrichCategoryPath(catelogId, stack);
        int size = stack.size();
        Long[] categoryPath = new Long[size];
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                categoryPath[i] = stack.pop();
            }
        }
        AttrVO attrVO = new AttrVO();
        BeanUtils.copyProperties(pmsAttr, attrVO);
        attrVO.setCatelogPath(categoryPath);


        PmsAttrAttrgroupRelationEntity pmsAttrAttrgroupRelationEntity =
                pmsAttrAttrgroupRelationDao.selectByAttrId(attrId);

        // 这个项目里，销售属性没有分组。因此这里要判空。
        if (Objects.nonNull(pmsAttrAttrgroupRelationEntity)) {
            attrVO.setAttrGroupId(pmsAttrAttrgroupRelationEntity.getAttrGroupId());
            attrVO.setAttrGroupName(pmsAttrGroupDao.selectById(pmsAttrAttrgroupRelationEntity.getAttrGroupId()).getAttrGroupName());
        }
        return attrVO;
    }

    @Override
    public void updateInfo(AttrVO pmsAttr) {
        Long attrId = pmsAttr.getAttrId();
        PmsAttrEntity byId = this.getById(attrId);
        BeanUtils.copyProperties(pmsAttr, byId);
        this.updateById(byId);

        Long attrGroupId = pmsAttr.getAttrGroupId();
        if (Objects.nonNull(attrGroupId)) {
            PmsAttrAttrgroupRelationEntity pmsAttrAttrgroupRelationEntity =
                    pmsAttrAttrgroupRelationDao.selectByAttrId(attrId);
            if (Objects.nonNull(pmsAttrAttrgroupRelationEntity)) {
                pmsAttrAttrgroupRelationEntity.setAttrGroupId(attrGroupId);
                pmsAttrAttrgroupRelationDao.updateById(pmsAttrAttrgroupRelationEntity);
            } else {
                PmsAttrAttrgroupRelationEntity insert = new PmsAttrAttrgroupRelationEntity();
                insert.setAttrId(attrId);
                insert.setAttrGroupId(attrGroupId);
                pmsAttrAttrgroupRelationDao.insert(insert);
            }
        }

    }
}
package com.xueliandan.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.common.utils.Query;
import com.xueliandan.gulimall.product.dao.PmsAttrAttrgroupRelationDao;
import com.xueliandan.gulimall.product.dao.PmsAttrDao;
import com.xueliandan.gulimall.product.dao.PmsAttrGroupDao;
import com.xueliandan.gulimall.product.entity.PmsAttrAttrgroupRelationEntity;
import com.xueliandan.gulimall.product.entity.PmsAttrEntity;
import com.xueliandan.gulimall.product.entity.PmsAttrGroupEntity;
import com.xueliandan.gulimall.product.entity.query.AttrQueryWrapper;
import com.xueliandan.gulimall.product.entity.vo.AttrAttrgroupRelationVO;
import com.xueliandan.gulimall.product.entity.vo.AttrVO;
import com.xueliandan.gulimall.product.entity.vo.GroupAttrVO;
import com.xueliandan.gulimall.product.service.PmsAttrGroupService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service("pmsAttrGroupService")
public class PmsAttrGroupServiceImpl extends ServiceImpl<PmsAttrGroupDao, PmsAttrGroupEntity> implements PmsAttrGroupService {


    @Autowired
    private PmsAttrAttrgroupRelationDao attrAttrGroupRelationDao;
    @Autowired
    private PmsAttrDao pmsAttrDao;
    @Autowired
    private PmsAttrGroupDao pmsAttrGroupDao;

    @Override

    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PmsAttrGroupEntity> page = this.page(
                new Query<PmsAttrGroupEntity>().getPage(params),
                new QueryWrapper<PmsAttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 约定前台传 0 查询所有，否则查询指定三级分类下的属性分组
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params, Long categoryId) {
        QueryWrapper<PmsAttrGroupEntity> queryWrapper = new QueryWrapper<>();
        Object o = params.get("key");
        if (o != null && StringUtils.hasLength(o.toString())) {
            queryWrapper.or().like("attr_group_name", o).or().like("descript", o);
        }
        if (categoryId == 0) {
            return new PageUtils(this.page(new Query<PmsAttrGroupEntity>().getPage(params), queryWrapper));
        }

        IPage<PmsAttrGroupEntity> page = this.page(
                new Query<PmsAttrGroupEntity>().getPage(params),
                queryWrapper.and(wrapper -> wrapper.eq("catelog_id", categoryId)));
        return new PageUtils(page);
    }

    @Override
    public List<AttrAttrgroupRelationVO> pageGroupAttrs(Long groupId) {

        List<PmsAttrAttrgroupRelationEntity> records = attrAttrGroupRelationDao.selectList(
                new QueryWrapper<PmsAttrAttrgroupRelationEntity>().eq("attr_group_id", groupId));


        List<AttrAttrgroupRelationVO> relationVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(records)) {
            Set<Long> attrIdSet = records.stream().map(PmsAttrAttrgroupRelationEntity::getAttrId)
                    .filter(Objects::nonNull).collect(Collectors.toSet());

            List<PmsAttrEntity> pmsAttrEntities = pmsAttrDao.selectByIds(attrIdSet);
            Map<Long, PmsAttrEntity> idEntityMap =
                    pmsAttrEntities.stream().collect(Collectors.toMap(PmsAttrEntity::getAttrId, Function.identity(), (attr1, attr2) -> attr1));
            records.forEach(record -> {
                AttrAttrgroupRelationVO relationVO = new AttrAttrgroupRelationVO();
                BeanUtils.copyProperties(record, relationVO);
                if (idEntityMap.containsKey(record.getAttrId())) {
                    PmsAttrEntity pmsAttrEntity = idEntityMap.get(record.getAttrId());
                    relationVO.setAttrName(pmsAttrEntity.getAttrName());
                    relationVO.setValueSelect(pmsAttrEntity.getValueSelect());
                }
                relationVOS.add(relationVO);
            });
        }
        return relationVOS;
    }

    @Override
    public void deleteGroupRelation(List<AttrQueryWrapper> relations) {
        if (CollectionUtils.isEmpty(relations)) return;

        for (AttrQueryWrapper relation : relations) {
            Long attrGroupId = relation.getAttrGroupId();
            Long attrId = relation.getAttrId();
            attrAttrGroupRelationDao.deleteByAttrGroupIdAndAttrId(attrGroupId, attrId);
        }
    }

    @Override
    public PageUtils groupNoRelation(Map<String, Object> params, Long attrGroupId) {
        if (null == attrGroupId) return null;

        PmsAttrGroupEntity attrGroup = this.getById(attrGroupId);
        if (null == attrGroup) {
            throw new IllegalArgumentException("分组被删除!");
        }

        // 拿到分组是哪个分类下的
        Long catelogId = attrGroup.getCatelogId();
        // 拿到分类下的所有分组
        List<PmsAttrGroupEntity> groupsUnderCategory = pmsAttrGroupDao.selectList(new QueryWrapper<PmsAttrGroupEntity>().eq("catelog_id", catelogId));
        // 拿到当前分类下其他分组已经绑定的 attrId
        Set<Long> existAttrIds = groupsUnderCategory.stream().map(PmsAttrGroupEntity::getAttrGroupId)
                .flatMap(groupId ->
                        attrAttrGroupRelationDao.selectList(new QueryWrapper<PmsAttrAttrgroupRelationEntity>().eq("attr_group_id", groupId))
                                .stream().map(PmsAttrAttrgroupRelationEntity::getAttrId))
                .collect(Collectors.toSet());

        QueryWrapper<PmsAttrEntity> queryWrapper = new QueryWrapper<>();
        // 当前分类下的所有基础属性，不包含销售属性
        queryWrapper.eq("catelog_id", catelogId).eq("attr_type", 1);

        if (!CollectionUtils.isEmpty(existAttrIds)) {
            // 查询属性分类表，找到未关联的属性
            queryWrapper.and(wrapper -> wrapper.notIn("attr_id", existAttrIds));
        }

        Object key = params.get("key");
        if (!Objects.equals("", key)) {
            queryWrapper.and(wrapper -> wrapper.eq("attr_id", key).or().like("attr_name", key));
        }

        IPage<PmsAttrEntity> pmsAttrEntityIPage = pmsAttrDao.selectPage(new Query<PmsAttrEntity>().getPage(params), queryWrapper);
        return new PageUtils(pmsAttrEntityIPage);
    }

    @Override
    public void batchSaveGroupRelation(List<AttrQueryWrapper> relations) {
        if (!CollectionUtils.isEmpty(relations)) {
            List<PmsAttrAttrgroupRelationEntity> relationEntities = relations.stream().map(relation -> {
                PmsAttrAttrgroupRelationEntity relationEntity = new PmsAttrAttrgroupRelationEntity();
                BeanUtils.copyProperties(relation, relationEntity);
                return relationEntity;
            }).collect(Collectors.toList());
            attrAttrGroupRelationDao.insert(relationEntities);
        }
    }

    @Override
    public List<GroupAttrVO> categoryGroupWithAttr(Long categoryId) {
        List<GroupAttrVO> groupAttrVOS = new ArrayList<>();
        List<PmsAttrGroupEntity> attrGroupEntities = pmsAttrGroupDao.selectList(new QueryWrapper<PmsAttrGroupEntity>().eq("catelog_id", categoryId));
        if (!CollectionUtils.isEmpty(attrGroupEntities)) {
            for (PmsAttrGroupEntity attrGroupEntity : attrGroupEntities) {
                Long attrGroupId = attrGroupEntity.getAttrGroupId();
                List<PmsAttrAttrgroupRelationEntity> groupAttrRelations = attrAttrGroupRelationDao.selectByAttrGroupId(attrGroupId);
                if (!CollectionUtils.isEmpty(groupAttrRelations)) {
                    GroupAttrVO groupAttrVO = new GroupAttrVO();
                    groupAttrVO.setAttrGroupId(attrGroupId);
                    groupAttrVO.setAttrGroupName(attrGroupEntity.getAttrGroupName());
                    groupAttrVO.setSort(attrGroupEntity.getSort());
                    groupAttrVO.setDescript(attrGroupEntity.getDescript());
                    groupAttrVO.setIcon(attrGroupEntity.getIcon());
                    groupAttrVO.setCatelogId(attrGroupEntity.getCatelogId());

                    List<AttrVO> attrVOS = new ArrayList<>();
                    Set<Long> attrIds = groupAttrRelations.stream().map(PmsAttrAttrgroupRelationEntity::getAttrId)
                            .filter(Objects::nonNull).collect(Collectors.toSet());
                    List<PmsAttrEntity> pmsAttrEntities = pmsAttrDao.selectByIds(attrIds);
                    if (!CollectionUtils.isEmpty(pmsAttrEntities)) {
                        pmsAttrEntities.forEach(pmsAttrEntity -> {
                            AttrVO attrVO = new AttrVO();
                            BeanUtils.copyProperties(pmsAttrEntity, attrVO);
                            attrVOS.add(attrVO);
                        });
                        groupAttrVO.setAttrs(attrVOS);
                        groupAttrVOS.add(groupAttrVO);
                    }
                }
            }
        }
        return groupAttrVOS;
    }

}
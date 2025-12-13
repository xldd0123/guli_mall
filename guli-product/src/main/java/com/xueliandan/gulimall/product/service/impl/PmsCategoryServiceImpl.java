package com.xueliandan.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.common.utils.Query;
import com.xueliandan.gulimall.product.dao.PmsCategoryDao;
import com.xueliandan.gulimall.product.entity.PmsAttrGroupEntity;
import com.xueliandan.gulimall.product.entity.PmsCategoryEntity;
import com.xueliandan.gulimall.product.service.PmsCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


@Service("pmsCategoryService")
public class PmsCategoryServiceImpl extends ServiceImpl<PmsCategoryDao, PmsCategoryEntity> implements PmsCategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PmsCategoryEntity> page = this.page(
                new Query<PmsCategoryEntity>().getPage(params),
                new QueryWrapper<PmsCategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<PmsCategoryEntity> listTree() {
        List<PmsCategoryEntity> allCategories = this.list();
        if (!CollectionUtils.isEmpty(allCategories)) {
            return allCategories.stream().filter(pmsCategoryEntity -> Objects.nonNull(pmsCategoryEntity.getParentCid()))
                    .filter(pmsCategoryEntity -> Objects.equals(pmsCategoryEntity.getParentCid(), 0L))
                    .peek(pmsCategoryEntity -> pmsCategoryEntity.setChildren(buildTree(pmsCategoryEntity, allCategories)))
                    .sorted(Comparator.comparing(PmsCategoryEntity::getSort, Comparator.nullsLast(Integer::compareTo)))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public void enrichCategoryPath(PmsAttrGroupEntity pmsAttrGroup) {
        Long categoryId = pmsAttrGroup.getCatelogId();
        Stack<Long> stack = new Stack<>();
        doEnrichCategoryPath(categoryId, stack);
        Long[] categoryPath = new Long[stack.size()];
        int stackSize = stack.size();
        for (int i = 0; i < stackSize; i++) {
            categoryPath[i] = stack.pop();
        }
        pmsAttrGroup.setCategoryPath(categoryPath);
    }

    @Override
    public void doEnrichCategoryPath(Long categoryId, Stack<Long> stack) {
        if (Objects.isNull(categoryId) || categoryId == 0) return;
        stack.push(categoryId);
        PmsCategoryEntity category = baseMapper.selectById(categoryId);
        if (Objects.nonNull(category) && Objects.nonNull(category.getParentCid())) {
            Long parentCid = category.getParentCid();
            doEnrichCategoryPath(parentCid, stack);
        }
    }

    @Override
    public List<PmsCategoryEntity> findFirstLevelCategory() {
        return baseMapper.selectList(new QueryWrapper<PmsCategoryEntity>().eq("cat_level", 1));
    }




    /**
     * 这种方式递归构建树结构，频繁查库，效率低下!
     *
     * @param firstLevelCategories 一级分类
     */
    private void buildTreeWithoutEffective(List<PmsCategoryEntity> firstLevelCategories) {
        if (CollectionUtils.isEmpty(firstLevelCategories)) return;
        for (PmsCategoryEntity firstLevelCategory : firstLevelCategories) {
            Long catId = firstLevelCategory.getCatId();
            List<PmsCategoryEntity> children = baseMapper.selectList(new QueryWrapper<PmsCategoryEntity>().eq("parent_cid", catId));
            firstLevelCategory.setChildren(children);
            if (!CollectionUtils.isEmpty(children)) {
                buildTreeWithoutEffective(children);
            }
        }
    }

    public List<PmsCategoryEntity> buildTree(PmsCategoryEntity root, List<PmsCategoryEntity> allCategories) {
        Long catId = root.getCatId();
        List<PmsCategoryEntity> children = allCategories.stream()
                .filter(pmsCategoryEntity -> pmsCategoryEntity.getParentCid().equals(catId))
                .sorted(Comparator.comparing(PmsCategoryEntity::getSort, Comparator.nullsLast(Integer::compareTo)))
                .collect(Collectors.toList());
        root.setChildren(children);
        if (!CollectionUtils.isEmpty(children)) {
            // 不断的往子分类中递归添加子分类
            for (PmsCategoryEntity child : children) {
                child.setChildren(buildTree(child, allCategories));
            }
        }
        // 返回的 children 在前面的递归中都将孩子节点都设置好了
        return children;
    }

}
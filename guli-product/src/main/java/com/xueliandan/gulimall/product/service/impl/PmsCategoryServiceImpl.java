package com.xueliandan.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.common.utils.Query;
import com.xueliandan.gulimall.product.dao.PmsCategoryDao;
import com.xueliandan.gulimall.product.entity.PmsAttrGroupEntity;
import com.xueliandan.gulimall.product.entity.PmsCategoryEntity;
import com.xueliandan.gulimall.product.entity.vo.Catelog2Vo;
import com.xueliandan.gulimall.product.service.PmsCategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


@Service("pmsCategoryService")
public class PmsCategoryServiceImpl extends ServiceImpl<PmsCategoryDao, PmsCategoryEntity> implements PmsCategoryService {

    private final Logger log = LoggerFactory.getLogger(PmsCategoryServiceImpl.class);

    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

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

    // TODO 堆外内存溢出异常 OutOfDirectMemoryError
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {

        // 一级分类是key，然后二级分类是 List value
        // 拿到一级分类
        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
        String catalogJson = stringValueOperations.get("catalogJson");
        if (StringUtils.isEmpty(catalogJson)) {
            log.info("缓存未命中, 查询数据库...");
            Map<String, List<Catelog2Vo>> catalogJsonFromDB = getCatalogJsonFromDB();
            stringValueOperations.set("catalogJson", JSON.toJSONString(catalogJsonFromDB));
            return catalogJsonFromDB;
        }
        log.info("缓存命中...直接返回");
        return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });
    }

    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDB() {
        Map<String, List<Catelog2Vo>> retVal = new HashMap<>();

        // spring 默认单例模式，因此 this 对象在容器中只有一份，多个线程共用一把锁，可用
        synchronized (this) {
            // DCL，再次检查缓存中是否存在，可能被其它线程查询数据库放入了
            String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
            if (StringUtils.isNotBlank(catalogJson)) {
                return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
                });
            }
            log.info("查询了数据库。。。");
            // 一级分类是key，然后二级分类是 List value
            // 拿到一级分类
            List<PmsCategoryEntity> allCategories = baseMapper.selectList(null);
            List<PmsCategoryEntity> secondCateLogs = allCategories.stream()
                    .filter(category -> category.getCatLevel().equals(2)).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(secondCateLogs)) return new HashMap<>();


            List<PmsCategoryEntity> thirdCateLogs = allCategories.stream()
                    .filter(category -> category.getCatLevel().equals(3)).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(thirdCateLogs)) return new HashMap<>();

            Map<Long, List<PmsCategoryEntity>> firstCatlogMap = secondCateLogs.stream().collect(Collectors.groupingBy(PmsCategoryEntity::getParentCid));
            Map<Long, List<PmsCategoryEntity>> secondCatlogMap = thirdCateLogs.stream().collect(Collectors.groupingBy(PmsCategoryEntity::getParentCid));
            firstCatlogMap.forEach((catId, children) -> {
                List<Catelog2Vo> catelog2Vos = children.stream().map(secondLevelCategory -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo();
                    catelog2Vo.setCatalog1Id(catId.toString());
                    catelog2Vo.setId(secondLevelCategory.getCatId().toString());
                    catelog2Vo.setName(secondLevelCategory.getName());

                    List<PmsCategoryEntity> thirdCatlogs = secondCatlogMap.get(secondLevelCategory.getCatId());
                    if (!CollectionUtils.isEmpty(thirdCatlogs)) {
                        List<Catelog2Vo.Category3Vo> category3Vos =
                                thirdCatlogs.stream().map(thirdLevelCategory -> {
                                    Catelog2Vo.Category3Vo category3Vo = new Catelog2Vo.Category3Vo();
                                    category3Vo.setCatalog2Id(secondLevelCategory.getCatId().toString());
                                    category3Vo.setId(thirdLevelCategory.getCatId().toString());
                                    category3Vo.setName(thirdLevelCategory.getName());
                                    return category3Vo;
                                }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(category3Vos);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
                retVal.put(catId.toString(), catelog2Vos);
            });

            stringRedisTemplate.opsForValue().set("catalogJson", JSON.toJSONString(retVal));
            return retVal;
        }
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
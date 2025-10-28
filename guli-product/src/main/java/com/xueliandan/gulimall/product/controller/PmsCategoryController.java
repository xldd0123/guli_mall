package com.xueliandan.gulimall.product.controller;

import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.common.utils.R;
import com.xueliandan.gulimall.product.dao.PmsCategoryBrandRelationDao;
import com.xueliandan.gulimall.product.entity.PmsCategoryEntity;
import com.xueliandan.gulimall.product.service.PmsCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 商品三级分类
 *
 * @author zxb
 * @email 1456992938@qq.com
 * @date 2025-04-23 23:50:22
 */
@RestController
@RequestMapping("/product/category")
//@CrossOrigin(origins = "http://localhost:8001", allowedHeaders = "*")
public class PmsCategoryController {

    @Autowired
    private PmsCategoryService pmsCategoryService;
    @Autowired
    private PmsCategoryBrandRelationDao pmsCategoryBrandRelationDao;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:pmscategory:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = pmsCategoryService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * TREE 列表
     */
    @RequestMapping("/tree-list")
    //@RequiresPermissions("product:pmscategory:list")
    public R list() {
        List<PmsCategoryEntity> treeList = pmsCategoryService.listTree();
        return R.ok().put("treeList", treeList);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    //@RequiresPermissions("product:pmscategory:info")
    public R info(@PathVariable("catId") Long catId) {
        PmsCategoryEntity pmsCategory = pmsCategoryService.getById(catId);

        return R.ok().put("category", pmsCategory);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("product:pmscategory:save")
    public R save(@RequestBody PmsCategoryEntity pmsCategory) {
        if (null != pmsCategory) {
            pmsCategoryService.save(pmsCategory);
        }

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/")
    @Transactional(rollbackFor = Exception.class)
    //@RequiresPermissions("product:pmscategory:update")
    public R update(@RequestBody PmsCategoryEntity pmsCategory) {
        if (null == pmsCategory || pmsCategory.getCatId() == null) {
            throw new IllegalArgumentException("修改时主键不能为空!");
        }
        pmsCategoryService.updateById(pmsCategory);
        // 同步修改品牌关联分类的名称
        pmsCategoryBrandRelationDao.updateCategoryName(pmsCategory.getCatId(), pmsCategory.getName());

        return R.ok();
    }

    @PutMapping("/batch")
    @Transactional(rollbackFor = Exception.class)
    public R batchUpdate(@RequestBody List<PmsCategoryEntity> pmsCategories) {
        if (CollectionUtils.isEmpty(pmsCategories)) return R.ok("集合为空，未做更新");
        pmsCategoryService.updateBatchById(pmsCategories);
        return R.ok();
    }

    /**
     * 单个删除
     */
    @DeleteMapping("/{catId}")
    //@RequiresPermissions("product:pmscategory:delete")
    @Transactional(rollbackFor = Exception.class)
    public R deleteById(@PathVariable Long catId) {
        pmsCategoryService.removeById(catId);
        return R.ok();
    }


    /**
     * 批量删除
     */
    @DeleteMapping("/batch")
    //@RequiresPermissions("product:pmscategory:delete")
    @Transactional(rollbackFor = Exception.class)
    public R batchDelete(@RequestBody Long[] catIds) {
        pmsCategoryService.removeByIds(Arrays.asList(catIds));
        return R.ok();
    }

}

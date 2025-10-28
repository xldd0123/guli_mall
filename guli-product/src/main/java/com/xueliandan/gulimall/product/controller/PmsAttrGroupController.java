package com.xueliandan.gulimall.product.controller;

import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.common.utils.R;
import com.xueliandan.gulimall.product.entity.PmsAttrGroupEntity;
import com.xueliandan.gulimall.product.entity.query.AttrQueryWrapper;
import com.xueliandan.gulimall.product.entity.vo.AttrAttrgroupRelationVO;
import com.xueliandan.gulimall.product.entity.vo.GroupAttrVO;
import com.xueliandan.gulimall.product.service.PmsAttrGroupService;
import com.xueliandan.gulimall.product.service.PmsCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 属性分组
 *
 * @author zxb
 * @email 1456992938@qq.com
 * @date 2025-04-23 23:50:23
 */
@RestController
@RequestMapping("product/attrgroup")
public class PmsAttrGroupController {

    @Autowired
    private PmsAttrGroupService pmsAttrGroupService;

    @Autowired
    private PmsCategoryService pmsCategoryService;

    /**
     * 列表
     */
    @RequestMapping("/list/{categoryId}")
    //@RequiresPermissions("product:pmsattrgroup:list")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable(name = "categoryId") Long categoryId) {
        PageUtils page = pmsAttrGroupService.queryPage(params, categoryId);
        return R.ok().put("page", page);
    }

    @GetMapping(path = "/{groupId}/attr/relation")
    public R groupAttr(@PathVariable(name = "groupId") Long groupId) {
        List<AttrAttrgroupRelationVO> data = pmsAttrGroupService.pageGroupAttrs(groupId);
        return R.ok().put("data", data);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:pmsattrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId) {
        PmsAttrGroupEntity pmsAttrGroup = pmsAttrGroupService.getById(attrGroupId);

        // 填充商品分类路径
        pmsCategoryService.enrichCategoryPath(pmsAttrGroup);


        return R.ok().put("pmsAttrGroup", pmsAttrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:pmsattrgroup:save")
    public R save(@RequestBody PmsAttrGroupEntity pmsAttrGroup) {
        pmsAttrGroupService.save(pmsAttrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:pmsattrgroup:update")
    public R update(@RequestBody PmsAttrGroupEntity pmsAttrGroup) {
        pmsAttrGroupService.updateById(pmsAttrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:pmsattrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds) {
        pmsAttrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

    @PostMapping(path = "/attr/relation")
    public R batchSaveGroupRelation(@RequestBody List<AttrQueryWrapper> relations) {

        pmsAttrGroupService.batchSaveGroupRelation(relations);
        return R.ok();
    }

    /**
     * 批量删除分组关联的属性
     */
    @PostMapping(path = "/attr/relation/delete")
    public R deleteGroupRelation(@RequestBody List<AttrQueryWrapper> relations) {
        pmsAttrGroupService.deleteGroupRelation(relations);
        return R.ok();
    }

    /**
     * 查询当前分类下，未被其他分组未关联的属性。必须是当前分类下的属性，且没有被其它分组关联过。
     */
    @GetMapping(path = "/{attrGroupId}/noattr/relation")
    public R groupNoRelation(@RequestParam Map<String, Object> params,
                             @PathVariable(name = "attrGroupId") Long attrGroupId) {
        PageUtils page = pmsAttrGroupService.groupNoRelation(params, attrGroupId);
        return R.ok().put("page", page);
    }

    /**
     * 查询出分类下所有的分组，且分组必须关联了属性
     */
    @GetMapping(path = "/{categoryId}/withattr")
    public R categoryGroupWithAttr(@PathVariable(name = "categoryId") Long categoryId) {
        List<GroupAttrVO> groupAttrVOS = pmsAttrGroupService.categoryGroupWithAttr(categoryId);
        return R.ok().put("data", groupAttrVOS);
    }

}

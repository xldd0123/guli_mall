package com.xueliandan.gulimall.product.entity.vo;

import lombok.Data;

/**
 * @author zxb 2025/10/9 11:12
 */
@Data
public class AttrAttrgroupRelationVO {


    private Long id;
    /**
     * 属性id
     */
    private Long attrId;

    /**
     * 属性名
     */
    private String attrName;

    /**
     * 属性可选值
     */
    private String valueSelect;
    /**
     * 属性分组id
     */
    private Long attrGroupId;
    /**
     * 属性组内排序
     */
    private Integer attrSort;

}

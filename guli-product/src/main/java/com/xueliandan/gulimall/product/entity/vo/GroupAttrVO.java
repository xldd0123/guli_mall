package com.xueliandan.gulimall.product.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * @author zxb 2025/10/12 13:26
 */
@Data
public class GroupAttrVO {
    private Long attrGroupId;

    private String attrGroupName;

    private Integer sort;

    private String descript;

    private String icon;

    private Long catelogId;

    private List<AttrVO> attrs;
}

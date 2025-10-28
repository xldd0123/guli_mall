package com.xueliandan.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xueliandan.gulimall.common.validator.anno.ListValue;
import com.xueliandan.gulimall.common.validator.group.AddGroup;
import com.xueliandan.gulimall.common.validator.group.UpdateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 品牌
 *
 * @author zxb
 * @email 1456992938@qq.com
 * @date 2025-04-23 23:50:23
 */
@Data
@TableName("pms_brand")
public class PmsBrandEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 品牌id
     */
    @TableId
    @Null(message = "新增时品牌 id 必须为空", groups = {AddGroup.class})
    @NotNull(message = "修改时品牌 id 必须不为空", groups = {UpdateGroup.class})
    private Long brandId;

    /**
     * 品牌名
     */
    @NotBlank(message = "品牌名 不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String name;

    /**
     * 品牌logo地址
     */
    @URL(message = "logo 必须是一个合法的URL地址", groups = {AddGroup.class, UpdateGroup.class})
    @NotBlank(message = "logo 不能为空", groups = {AddGroup.class})
    private String logo;

    /**
     * 介绍
     */
    private String descript;

    /**
     * 显示状态[0-不显示；1-显示]
     * 不能对非 CharSequence 类型使用 @Pattern 直接，否则抛错
     */
//    @NotNull(message = "显示状态 不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @ListValue(groups = {AddGroup.class, UpdateGroup.class})
    private Integer showStatus;

    /**
     * 检索首字母
     */
    @NotBlank(message = "检索首字母不能为空")
    @Pattern(regexp = "^[a-zA-Z]$", message = "检索首字母必须是一个字母", groups = {AddGroup.class, UpdateGroup.class})
    private String firstLetter;
    /**
     * 排序
     */
    @NotNull(message = "排序值不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Min(value = 0, message = "排序必须大于等于0", groups = {AddGroup.class, UpdateGroup.class})
    private Integer sort;
}

package com.xueliandan.gulimall.ware.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * @author zxb 2025/10/20 10:49
 */
@Data
public class MergeVO {
    private Long purchaseId;
    /**
     * 采购需求 id 集合
     */
    private List<Long> items;
}

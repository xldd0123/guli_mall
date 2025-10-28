package com.xueliandan.gulimall.ware.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * @author zxb 2025/10/21 17:10
 */
@Data
public class PurchaseFinishedVO {

    /**
     * 采购单 id
     */
    private Long id;
    /**
     * 采购项集合
     */
    private List<PurchaseItemVO> items;
}

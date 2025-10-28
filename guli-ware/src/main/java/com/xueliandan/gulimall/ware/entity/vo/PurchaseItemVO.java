package com.xueliandan.gulimall.ware.entity.vo;

import com.xueliandan.gulimall.common.enums.ware.PurchaseStatusEnum;
import lombok.Data;

/**
 * @author zxb 2025/10/21 17:11
 */
@Data
public class PurchaseItemVO {

    /**
     * 采购项 id
     */
    private Long itemId;
    /**
     * @see com.xueliandan.gulimall.common.enums.ware.PurchaseDemandStatusEnum
     */
    private Integer status;
    /**
     * 采购失败原因
     */
    private String reason;
}

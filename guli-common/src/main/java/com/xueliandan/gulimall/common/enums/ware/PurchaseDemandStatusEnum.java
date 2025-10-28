package com.xueliandan.gulimall.common.enums.ware;

import lombok.Data;
import lombok.Getter;

/**
 * 采购需求状态枚举
 *
 * @author zxb 2025/10/20 11:11
 */
@Getter
public enum PurchaseDemandStatusEnum {

    NEW(0, "新建"),

    ASSIGNED(1, "已分配"),

    PURCHASING(2, "正在采购"),

    FINISHED(3, "已完成"),

    PURCHASE_FAILED(4, "采购失败");

    private final int code;
    private final String message;

    PurchaseDemandStatusEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

package com.xueliandan.gulimall.common.enums.ware;

import lombok.Getter;

/**
 * 采购单状态枚举
 *
 * @author zxb 2025/10/20 11:02
 */
@Getter
public enum PurchaseStatusEnum {

    NEW(0, "新建"),
    ASSIGNED(1, "已分配"),
    RECEIVED(2, "已领取"),
    FINISHED(3, "已完成"),
    HAS_ERROR(4, "有异常");

    PurchaseStatusEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private final int code;
    private final String message;

}

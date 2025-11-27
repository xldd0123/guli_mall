package com.xueliandan.gulimall.common.enums.product;

import lombok.Getter;

/**
 * @author zxb 2025/11/21 19:20
 */
@Getter
public enum ProductPublisheStatusEnum {
    NEW(-1, "新建"),
    PUBLISHED(1, "已上架"),
    DOWN(2, "下架");


    private final int code;
    private final String msg;

    ProductPublisheStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}

package com.xueliandan.gulimall.search.api.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zxb 2025/11/3 20:53
 */
@Data
public class EsAttrModel  implements Serializable {

    private Long attrId;

    private String attrName;

    private String attrValue;
}

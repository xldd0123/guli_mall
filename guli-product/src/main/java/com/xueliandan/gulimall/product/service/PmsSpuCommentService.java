package com.xueliandan.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.product.entity.PmsSpuCommentEntity;

import java.util.Map;

/**
 * 商品评价
 *
 * @author zxb
 * @email 1456992938@qq.com
 * @date 2025-04-23 23:50:21
 */
public interface PmsSpuCommentService extends IService<PmsSpuCommentEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


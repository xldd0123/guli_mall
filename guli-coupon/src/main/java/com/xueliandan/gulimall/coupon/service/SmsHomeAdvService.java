package com.xueliandan.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.coupon.entity.SmsHomeAdvEntity;

import java.util.Map;

/**
 * 首页轮播广告
 *
 * @author zxb
 * @email 1456992938@qq.com
 * @date 2025-04-23 23:11:59
 */
public interface SmsHomeAdvService extends IService<SmsHomeAdvEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


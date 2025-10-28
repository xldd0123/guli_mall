package com.xueliandan.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.common.utils.Query;
import com.xueliandan.gulimall.ware.dao.WmsWareInfoDao;
import com.xueliandan.gulimall.ware.entity.WmsWareInfoEntity;
import com.xueliandan.gulimall.ware.service.WmsWareInfoService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;


@Service("wmsWareInfoService")
public class WmsWareInfoServiceImpl extends ServiceImpl<WmsWareInfoDao, WmsWareInfoEntity> implements WmsWareInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<WmsWareInfoEntity> queryWrapper = new QueryWrapper<>();
        Object o = params.get("key");
        if (Objects.nonNull(o)) {
            String key = (String) o;
            queryWrapper.eq("id", key).or().like("name", key).or().like("address", key)
                    .or().like("areacode", key);
        }

        IPage<WmsWareInfoEntity> page = this.page(
                new Query<WmsWareInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

}
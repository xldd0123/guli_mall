package com.xueliandan.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.common.utils.Query;
import com.xueliandan.gulimall.product.dao.PmsSkuInfoDao;
import com.xueliandan.gulimall.product.entity.PmsSkuInfoEntity;
import com.xueliandan.gulimall.product.service.PmsSkuInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;


@Service("pmsSkuInfoService")
public class PmsSkuInfoServiceImpl extends ServiceImpl<PmsSkuInfoDao, PmsSkuInfoEntity> implements PmsSkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<PmsSkuInfoEntity> queryWrapper = new QueryWrapper<>();
        String catelogKey = "catelogId";
        if (params.containsKey(catelogKey) && !Objects.equals(params.get(catelogKey), "0")) {
            queryWrapper.eq("catalog_id", params.get(catelogKey));
        }
        String brandIdKey = "brandId";
        if (params.containsKey(brandIdKey) && !Objects.equals(params.get(brandIdKey), "0")) {
            queryWrapper.eq("brand_id", params.get("brandId"));
        }

        String minKey = "min";
        if (params.containsKey(minKey)) {
            queryWrapper.ge("price", params.get(minKey));
        }

        String maxKey = "max";
        if (params.containsKey(maxKey)) {
            queryWrapper.le("price", params.get(maxKey));
        }


        String key = "key";
        String keyVal = (String) params.get(key);
        if (params.containsKey(key) && !StringUtils.isEmpty(keyVal)) {
            // 这里的 wrapper 不能和上面的 wrapper 直接拼接
            // 直接拼接譬如 publish_status = 1  and spu_name like '%key%' or id = keyVal
            // 直接拼的化，后面的 or 成立，则前面的 publish_status !=1 的也会被查出来，影响查询结果。
            // 因此这段 spu_name like '%key%' or id = keyVal 要用 () 括起来，在代码中的提现就是通过 lambda 包起来即可。
            queryWrapper.and(wrapper ->
                    wrapper.like("sku_name", keyVal).or().eq("sku_id", keyVal));
        }

        IPage<PmsSkuInfoEntity> page = this.page(
                new Query<PmsSkuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

}
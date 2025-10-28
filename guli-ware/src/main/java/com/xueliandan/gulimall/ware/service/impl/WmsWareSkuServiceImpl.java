package com.xueliandan.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.common.utils.Query;
import com.xueliandan.gulimall.common.utils.R;
import com.xueliandan.gulimall.product.api.feign.SkuInfoFeignApi;
import com.xueliandan.gulimall.ware.dao.WmsWareSkuDao;
import com.xueliandan.gulimall.ware.entity.WmsWareSkuEntity;
import com.xueliandan.gulimall.ware.service.WmsWareSkuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;


@Service("wmsWareSkuService")
public class WmsWareSkuServiceImpl extends ServiceImpl<WmsWareSkuDao, WmsWareSkuEntity> implements WmsWareSkuService {

    @Autowired
    private SkuInfoFeignApi skuInfoFeignApi;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<WmsWareSkuEntity> queryWrapper = new QueryWrapper<>();

        try {
            String wareId = (String) params.get("wareId");
            if (StringUtils.isNotBlank(wareId)) {
                queryWrapper.eq("ware_id", wareId);
            }
            String skuId = (String) params.get("skuId");
            if (StringUtils.isNotBlank(skuId)) {
                queryWrapper.eq("sku_id", skuId);
            }
        } catch (Exception e) {
            // log  转换类型失败
            System.out.println(e.getMessage());
        }

        IPage<WmsWareSkuEntity> page = this.page(
                new Query<WmsWareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long wareId, Long skuId, Integer skuNum) {

        QueryWrapper<WmsWareSkuEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sku_id", skuId).eq("ware_id", wareId);
        WmsWareSkuEntity wmsWareSkuEntity = this.baseMapper.selectOne(queryWrapper);

        if (Objects.nonNull(wmsWareSkuEntity)) {
            wmsWareSkuEntity.setStock(wmsWareSkuEntity.getStock() + skuNum);
            this.updateById(wmsWareSkuEntity);
        } else {
            WmsWareSkuEntity toSaved = new WmsWareSkuEntity();
            toSaved.setWareId(wareId);
            toSaved.setSkuId(skuId);
            toSaved.setStock(skuNum);
            try {
                R info = skuInfoFeignApi.info(skuId);
                Map map = (Map) info.get("data");
                toSaved.setSkuName((String) map.get("skuName"));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            // 锁定库存默认设置为 0
            toSaved.setStockLocked(0);
            this.save(toSaved);
        }


    }

}
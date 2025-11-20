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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


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

    @Override
    public Map<String, Boolean> skusHasStock(List<Long> skuIds) {
        Map<String, Boolean> retVal = new HashMap<>();
        if (CollectionUtils.isEmpty(skuIds)) return retVal;

        List<WmsWareSkuEntity> wareSkuEntities = this.baseMapper.selectList(new QueryWrapper<WmsWareSkuEntity>().in("sku_id", skuIds));
        if (CollectionUtils.isNotEmpty(wareSkuEntities)) {
            // 一个 sku 可能位于多个仓库，所以这里根据 sku 聚合一下
            Map<Long, List<WmsWareSkuEntity>> skuIdMap = wareSkuEntities.stream().collect(Collectors.groupingBy(WmsWareSkuEntity::getSkuId));
            skuIdMap.forEach((skuId, wareSkus) -> {
                // 库存减去已锁定的库存，剩余的才是真实的库存。 已锁定的库存就是下单了但是还没支付
                Integer stock = wareSkuEntities.stream().map(WmsWareSkuEntity::getStock).reduce(0, Integer::sum);
                Integer lockStock = wareSkuEntities.stream().map(WmsWareSkuEntity::getStockLocked).reduce(0, Integer::sum);
                retVal.put(skuId.toString(), (stock - lockStock) > 0);
            });

        }
        return retVal;
    }

}
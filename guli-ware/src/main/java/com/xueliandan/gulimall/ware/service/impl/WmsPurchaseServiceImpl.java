package com.xueliandan.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xueliandan.gulimall.common.enums.ware.PurchaseDemandStatusEnum;
import com.xueliandan.gulimall.common.enums.ware.PurchaseStatusEnum;
import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.common.utils.Query;
import com.xueliandan.gulimall.ware.dao.WmsPurchaseDao;
import com.xueliandan.gulimall.ware.dao.WmsPurchaseDetailDao;
import com.xueliandan.gulimall.ware.entity.WmsPurchaseDetailEntity;
import com.xueliandan.gulimall.ware.entity.WmsPurchaseEntity;
import com.xueliandan.gulimall.ware.entity.vo.MergeVO;
import com.xueliandan.gulimall.ware.entity.vo.PurchaseFinishedVO;
import com.xueliandan.gulimall.ware.entity.vo.PurchaseItemVO;
import com.xueliandan.gulimall.ware.service.WmsPurchaseService;
import com.xueliandan.gulimall.ware.service.WmsWareSkuService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 不要忘了事务注解!!!!
 */
@Service("wmsPurchaseService")
public class WmsPurchaseServiceImpl extends ServiceImpl<WmsPurchaseDao, WmsPurchaseEntity> implements WmsPurchaseService {


    @Autowired
    private WmsPurchaseDetailDao wmsPurchaseDetailDao;
    @Autowired
    private WmsWareSkuService wmsWareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WmsPurchaseEntity> page = this.page(
                new Query<WmsPurchaseEntity>().getPage(params),
                new QueryWrapper<WmsPurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils listUnReceivePurchase(Map<String, Object> params) {
        QueryWrapper<WmsPurchaseEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> {
            wrapper.eq("status", 0).or().eq("status", 1);
        });
        IPage<WmsPurchaseEntity> page = this.page(
                new Query<WmsPurchaseEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    /**
     * 如果采购需求已经处于非新建和非已分配状态，那么不能再对其合并整单了。
     * 只有新建、已分配才能合并到采购单重，新建就是直接将采购需求合并到采购单中。
     * 已分配是将采购需求合并到其它采购单中，做的是更新操作。
     * 但是正在采购的以及采购完成就不能再合并到采购单中了，要做个限制。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void mergePurchase(MergeVO mergeVO) {
        if (null == mergeVO) return;
        List<Long> items = mergeVO.getItems();
        if (CollectionUtils.isEmpty(items)) return;

        // 采购需求 id, (wms_purchase_detail)
        List<WmsPurchaseDetailEntity> wmsPurchaseDetailEntities = wmsPurchaseDetailDao.selectByIds(items);
        if (CollectionUtils.isNotEmpty(wmsPurchaseDetailEntities)) {


            Long purchaseId = mergeVO.getPurchaseId();
            if (null == purchaseId) {
                WmsPurchaseEntity wmsPurchase = new WmsPurchaseEntity();
                wmsPurchase.setCreateTime(new Date());
                wmsPurchase.setUpdateTime(new Date());
                wmsPurchase.setStatus(PurchaseStatusEnum.NEW.getCode());
                this.save(wmsPurchase);
                purchaseId = wmsPurchase.getId();
            }


            for (WmsPurchaseDetailEntity wmsPurchaseDetailEntity : wmsPurchaseDetailEntities) {
                if (Stream.of(PurchaseDemandStatusEnum.NEW.getCode(), PurchaseDemandStatusEnum.ASSIGNED.getCode())
                        .noneMatch(code -> code.equals(wmsPurchaseDetailEntity.getStatus()))) {
                    throw new IllegalArgumentException("采购需求状态非新建和已分配，无法合并整单!!!");
                }
                wmsPurchaseDetailEntity.setPurchaseId(purchaseId);
                wmsPurchaseDetailEntity.setStatus(PurchaseDemandStatusEnum.ASSIGNED.getCode());
            }
            wmsPurchaseDetailDao.updateById(wmsPurchaseDetailEntities);


            WmsPurchaseEntity wmsPurchase = new WmsPurchaseEntity();
            wmsPurchase.setId(purchaseId);
            wmsPurchase.setUpdateTime(new Date());
            this.updateById(wmsPurchase);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void received(List<Long> ids) {

        if (CollectionUtils.isEmpty(ids)) return;

        List<WmsPurchaseEntity> wmsPurchaseEntities = this.listByIds(ids);
        for (WmsPurchaseEntity wmsPurchaseEntity : wmsPurchaseEntities) {
            Integer status = wmsPurchaseEntity.getStatus();
            // 采购单必须是新建或者未被领取的。
            if (Stream.of(PurchaseStatusEnum.NEW.getCode(), PurchaseStatusEnum.ASSIGNED.getCode()).noneMatch(code -> code.equals(status))) {
                throw new IllegalArgumentException("id 为" + wmsPurchaseEntity.getId() + "的采购单已经被领取!");
            }
        }
        // 领取采购单， 第一，采购单的状态需要修改。
        wmsPurchaseEntities.forEach(wmsPurchaseEntity -> {
            wmsPurchaseEntity.setStatus(PurchaseStatusEnum.RECEIVED.getCode());
            wmsPurchaseEntity.setUpdateTime(new Date());
        });
        this.updateBatchById(wmsPurchaseEntities);
        // 第二，采购单对应的采购需求状态也要修改为采购中。
        List<WmsPurchaseDetailEntity> wmsPurchaseDetailEntities = wmsPurchaseDetailDao.selectByPurchaseIds(ids);
        Map<Long, List<WmsPurchaseDetailEntity>> collect = wmsPurchaseDetailEntities.stream().collect(Collectors.groupingBy(WmsPurchaseDetailEntity::getPurchaseId));

        collect.forEach((purchaseId, entities) -> {
            entities.forEach(entity -> entity.setStatus(PurchaseDemandStatusEnum.PURCHASING.getCode()));
        });

        wmsPurchaseDetailDao.updateById(wmsPurchaseDetailEntities);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishPurchase(PurchaseFinishedVO purchaseFinishedVO) {

        // 完成采购
        Long purchaseId = purchaseFinishedVO.getId();
        WmsPurchaseEntity wmsPurchaseEntity = this.getById(purchaseId);
        if (null == wmsPurchaseEntity) throw new IllegalArgumentException("id 为" + purchaseId + "的采购单不存在!");


        List<PurchaseItemVO> items = purchaseFinishedVO.getItems();
        List<WmsPurchaseDetailEntity> successItems = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(items)) {
            // 1.修改采购单状态，如果所有采购项都正常完成，则采购单状态为已完成，如果有一项采购需求采购失败，则采购单的状态为有异常
            if (items.stream().anyMatch(item -> item.getStatus().equals(PurchaseDemandStatusEnum.PURCHASE_FAILED.getCode()))) {
                wmsPurchaseEntity.setStatus(PurchaseStatusEnum.HAS_ERROR.getCode());
            } else {
                wmsPurchaseEntity.setStatus(PurchaseStatusEnum.FINISHED.getCode());
            }
            wmsPurchaseEntity.setUpdateTime(new Date());
            this.updateById(wmsPurchaseEntity);

            // 2.根据采购结果，修改采购需求状态
            Map<Long, Integer> itemIdAndStatusMap = items.stream().collect(Collectors.toMap(PurchaseItemVO::getItemId, PurchaseItemVO::getStatus));
            List<WmsPurchaseDetailEntity> wmsPurchaseDetailEntities = wmsPurchaseDetailDao.selectByIds(itemIdAndStatusMap.keySet());
            for (WmsPurchaseDetailEntity wmsPurchaseDetailEntity : wmsPurchaseDetailEntities) {
                if (itemIdAndStatusMap.containsKey(wmsPurchaseDetailEntity.getId())) {
                    Integer status = itemIdAndStatusMap.get(wmsPurchaseDetailEntity.getId());
                    if (Objects.nonNull(status)) {
                        wmsPurchaseDetailEntity.setStatus(status);
                        if (status == PurchaseDemandStatusEnum.FINISHED.getCode()) {
                            successItems.add(wmsPurchaseDetailEntity);
                        }
                    }
                }
            }
            wmsPurchaseDetailDao.updateById(wmsPurchaseDetailEntities);
        }
        // 3.修改对应 sku 的库存，给库存加上采购的数量
        if (CollectionUtils.isNotEmpty(successItems)) {
            successItems.forEach(item -> {
                Long wareId = item.getWareId();
                Long skuId = item.getSkuId();
                Integer skuNum = item.getSkuNum();
                wmsWareSkuService.addStock(wareId, skuId, skuNum);
            });
        }

    }

}
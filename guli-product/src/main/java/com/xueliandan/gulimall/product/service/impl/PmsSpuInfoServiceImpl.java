package com.xueliandan.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.common.utils.Query;
import com.xueliandan.gulimall.coupon.api.dto.SkuFullReductionDTO;
import com.xueliandan.gulimall.coupon.api.dto.SkuLadderDTO;
import com.xueliandan.gulimall.coupon.api.dto.SmsMemberPriceDTO;
import com.xueliandan.gulimall.coupon.api.dto.SpuBoundsDTO;
import com.xueliandan.gulimall.coupon.api.feign.MemberPriceFeignApi;
import com.xueliandan.gulimall.coupon.api.feign.SkuFullReductionFeignApi;
import com.xueliandan.gulimall.coupon.api.feign.SkuLadderFeignApi;
import com.xueliandan.gulimall.coupon.api.feign.SpuBoundsFeignApi;
import com.xueliandan.gulimall.product.dao.*;
import com.xueliandan.gulimall.product.entity.*;
import com.xueliandan.gulimall.product.entity.vo.*;
import com.xueliandan.gulimall.product.service.PmsSkuInfoService;
import com.xueliandan.gulimall.product.service.PmsSpuInfoService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;


@Service("pmsSpuInfoService")
public class PmsSpuInfoServiceImpl extends ServiceImpl<PmsSpuInfoDao, PmsSpuInfoEntity> implements PmsSpuInfoService {


    @Autowired
    private PmsSpuInfoDescDao pmsSpuInfoDescDao;
    @Autowired
    private PmsSpuImagesDao pmsSpuImagesDao;
    @Autowired
    private PmsAttrDao pmsAttrDao;
    @Autowired
    private PmsProductAttrValueDao pmsProductAttrValueDao;
    @Autowired
    private PmsSkuImagesDao pmsSkuImagesDao;
    @Autowired
    private PmsSkuSaleAttrValueDao pmsSkuSaleAttrValueDao;

    @Autowired
    private SkuLadderFeignApi skuLadderFeignApi;
    @Autowired
    private SkuFullReductionFeignApi skuFullReductionFeignApi;
    @Autowired
    private MemberPriceFeignApi memberPriceFeignApi;
    @Autowired
    private SpuBoundsFeignApi boundsFeignApi;

    @Autowired
    private PmsSkuInfoService pmsSkuInfoService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PmsSpuInfoEntity> queryWrapper = new QueryWrapper<>();
        String catelogKey = "catelogId";
        if (params.containsKey(catelogKey) && !Objects.equals(params.get(catelogKey), "0")) {
            queryWrapper.eq("catalog_id", params.get(catelogKey));
        }
        String brandIdKey = "brandId";
        if (params.containsKey(brandIdKey) && !Objects.equals(params.get(brandIdKey), "0")) {
            queryWrapper.eq("brand_id", params.get("brandId"));
        }

        String publishStatusKey = "status";
        if (params.containsKey(publishStatusKey) && !Objects.equals(params.get(publishStatusKey), "0")) {
            queryWrapper.eq("publish_status", params.get("status"));
        }

        String key = "key";
        String keyVal = (String) params.get(key);
        if (params.containsKey(key) && !StringUtils.isEmpty(keyVal)) {
            // 这里的 wrapper 不能和上面的 wrapper 直接拼接
            // 直接拼接譬如 publish_status = 1  and spu_name like '%key%' or id = keyVal
            // 直接拼的化，后面的 or 成立，则前面的 publish_status !=1 的也会被查出来，影响查询结果。
            // 因此这段 spu_name like '%key%' or id = keyVal 要用 () 括起来，在代码中的提现就是通过 lambda 包起来即可。
            queryWrapper.and(wrapper ->
                    wrapper.like("spu_name", keyVal).or().eq("id", keyVal));
        }


        IPage<PmsSpuInfoEntity> page = this.page(
                new Query<PmsSpuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 在本系统中，bounds 积分信息，是在发布商品也就是 SPU 的级别设置的，
     * 那么购买 SPU 下所有的 SKU 获得的积分都是一样的。
     * <p>
     * 当然，其它的业务系统，也可以在 SKU 的级别设置积分信息，这样购买不同的 SKU 就可以获得不同的积分。
     * 都是可以的。
     * <p>
     * TODO 目前只走了成功的逻辑，如果失败了，需要跨服务回滚。后续再完善。
     *
     * @param spuSaveVo spu 信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSpuInfoVO(SpuSaveVo spuSaveVo) {
        // 校验先不做了

        // 1.保存 SPU 基本信息 pms_spu_info
        PmsSpuInfoEntity infoEntity = new PmsSpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, infoEntity);
        infoEntity.setCreateTime(new Date());
        infoEntity.setUpdateTime(new Date());
        this.saveSpuInfo(infoEntity);

        // 2.保存 SPU 描述图片 pms_spu_info_desc
        List<String> decript = spuSaveVo.getDecript();
        if (CollectionUtils.isNotEmpty(decript)) {
            PmsSpuInfoDescEntity spuInfoDescEntity = new PmsSpuInfoDescEntity();
            spuInfoDescEntity.setSpuId(infoEntity.getId());
            spuInfoDescEntity.setDecript(String.join(",", decript));
            this.saveSpuDescInfo(spuInfoDescEntity);
        }

        // 3.保存 SPU 图片集 pms_spu_images
        List<String> spuImages = spuSaveVo.getImages();
        if (CollectionUtils.isNotEmpty(spuImages)) {
            List<PmsSpuImagesEntity> pmsSpuImagesEntities = new ArrayList<>();
            for (String image : spuImages) {
                if (StringUtils.isNotBlank(image)) {
                    PmsSpuImagesEntity spuImagesEntity = new PmsSpuImagesEntity();
                    spuImagesEntity.setImgUrl(image);
                    spuImagesEntity.setSpuId(infoEntity.getId());
                    pmsSpuImagesEntities.add(spuImagesEntity);
                }
            }
            this.saveSpuImages(pmsSpuImagesEntities);
        }

        // 4.保存 SPU 的规格参数信息 pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
        if (CollectionUtils.isNotEmpty(baseAttrs)) {
            List<PmsProductAttrValueEntity> pmsProductAttrValueEntities = new ArrayList<>();
            for (BaseAttrs baseAttr : baseAttrs) {
                PmsProductAttrValueEntity pmsProductAttrValueEntity = new PmsProductAttrValueEntity();
                Long attrId = baseAttr.getAttrId();
                String attrValues = baseAttr.getAttrValues();
                PmsAttrEntity pmsAttrEntity = pmsAttrDao.selectById(attrId);
                pmsProductAttrValueEntity.setSpuId(infoEntity.getId());
                pmsProductAttrValueEntity.setAttrId(attrId);
                pmsProductAttrValueEntity.setAttrName(pmsAttrEntity.getAttrName());
                pmsProductAttrValueEntity.setAttrValue(attrValues);
                pmsProductAttrValueEntity.setQuickShow(baseAttr.getShowDesc());
                pmsProductAttrValueEntities.add(pmsProductAttrValueEntity);
            }
            this.saveSpuBaseAttrs(pmsProductAttrValueEntities);
        }

        // 5.保存 SPU 的积分信息 gulimall_sms -> sms_spu_bounds
        Bounds bounds = spuSaveVo.getBounds();
        if (bounds != null) {
            SpuBoundsDTO spuBoundsDTO = new SpuBoundsDTO();
            spuBoundsDTO.setSpuId(infoEntity.getId());
            spuBoundsDTO.setBuyBounds(bounds.getBuyBounds());
            spuBoundsDTO.setGrowBounds(bounds.getGrowBounds());
            boundsFeignApi.save(spuBoundsDTO);
        }

        // 6.保存 SPU 下所有的 SKU 信息 pms_sku_info
        List<SkuVO> skuVOS = spuSaveVo.getSkus();

        if (CollectionUtils.isNotEmpty(skuVOS)) {
            for (SkuVO skuVO : skuVOS) {
                // 6.1 保存 SKU 的基本信息 pms_sku_info
                PmsSkuInfoEntity pmsSkuInfoEntity = new PmsSkuInfoEntity();
                BeanUtils.copyProperties(skuVO, pmsSkuInfoEntity);
                pmsSkuInfoEntity.setCatalogId(infoEntity.getCatalogId());
                pmsSkuInfoEntity.setBrandId(infoEntity.getBrandId());
                pmsSkuInfoEntity.setSpuId(infoEntity.getId());
                // 销量默认给个 0
                pmsSkuInfoEntity.setSaleCount(0L);
                // 找到默认图片并设值
                Optional.ofNullable(skuVO.getImages()).flatMap(images -> images.stream().filter(skuImg -> Objects.nonNull(skuImg) && skuImg.getDefaultImg() == 1)
                        .findFirst()).ifPresent(skuImg -> pmsSkuInfoEntity.setSkuDefaultImg(skuImg.getImgUrl()));
                pmsSkuInfoService.save(pmsSkuInfoEntity);

                // 6.2 保存 SKU 的图片信息 pms_sku_images
                List<Images> skuImages = skuVO.getImages();
                List<PmsSkuImagesEntity> skuImagesEntities = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(skuImages)) {
                    for (Images skuImage : skuImages) {
                        // 去除掉图集中未选则的图片
                        if (StringUtils.isNotBlank(skuImage.getImgUrl())) {
                            PmsSkuImagesEntity pmsSkuImagesEntity = new PmsSkuImagesEntity();
                            pmsSkuImagesEntity.setImgUrl(skuImage.getImgUrl());
                            pmsSkuImagesEntity.setSkuId(pmsSkuInfoEntity.getSkuId());
                            Integer defaultImg = skuImage.getDefaultImg();
                            pmsSkuImagesEntity.setDefaultImg(defaultImg);
                            skuImagesEntities.add(pmsSkuImagesEntity);
                        }
                    }
                    if (CollectionUtils.isNotEmpty(skuImagesEntities)) {
                        pmsSkuImagesDao.insert(skuImagesEntities);
                    }
                }


                // 6.3 保存 SKU 的销售属性信息 pms_sku_sale_attr_value
                List<Attr> saleAttrs = skuVO.getAttr();
                if (CollectionUtils.isNotEmpty(saleAttrs)) {
                    List<PmsSkuSaleAttrValueEntity> pmsSkuSaleAttrValueEntities = new ArrayList<>();
                    for (Attr saleAttr : saleAttrs) {
                        PmsSkuSaleAttrValueEntity skuSaleAttrValueEntity = new PmsSkuSaleAttrValueEntity();
                        BeanUtils.copyProperties(saleAttr, skuSaleAttrValueEntity);
                        skuSaleAttrValueEntity.setSkuId(pmsSkuInfoEntity.getSkuId());
                        pmsSkuSaleAttrValueEntities.add(skuSaleAttrValueEntity);
                    }
                    pmsSkuSaleAttrValueDao.insert(pmsSkuSaleAttrValueEntities);
                }


                // 7.保存优惠相关数据
                // 7.1 保存满几件打几折信息
                if (Objects.nonNull(skuVO.getFullCount())
                        && Objects.nonNull(skuVO.getDiscount())
                        && skuVO.getFullCount() > 0
                        && skuVO.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
                    SkuLadderDTO skuLadderDTO = new SkuLadderDTO();
                    BeanUtils.copyProperties(skuVO, skuLadderDTO);
                    skuLadderDTO.setSkuId(pmsSkuInfoEntity.getSkuId());
                    skuLadderDTO.setAddOther(skuVO.getCountStatus());
                    // 折后价可以在下单后再计算，这里先不计算。
                    skuLadderFeignApi.save(skuLadderDTO);
                }


                // 7.2 保存满减信息
                if (Objects.nonNull(skuVO.getFullPrice())
                        && Objects.nonNull(skuVO.getReducePrice())
                        && skuVO.getFullPrice().compareTo(BigDecimal.ZERO) > 0
                        && skuVO.getReducePrice().compareTo(BigDecimal.ZERO) > 0) {
                    SkuFullReductionDTO skuFullReductionDTO = new SkuFullReductionDTO();
                    BeanUtils.copyProperties(skuVO, skuFullReductionDTO);
                    skuFullReductionDTO.setSkuId(pmsSkuInfoEntity.getSkuId());
                    skuFullReductionFeignApi.save(skuFullReductionDTO);
                }


                // 7.3 保存会员价格信息
                List<MemberPrice> memberPrices = skuVO.getMemberPrice();
                if (CollectionUtils.isNotEmpty(memberPrices)) {
                    for (MemberPrice memberPrice : memberPrices) {
                        if (Objects.nonNull(memberPrice.getPrice())
                                && memberPrice.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                            SmsMemberPriceDTO memberPriceDTO = new SmsMemberPriceDTO();
                            memberPriceDTO.setMemberLevelId(memberPrice.getId());
                            memberPriceDTO.setMemberLevelName(memberPrice.getName());
                            memberPriceDTO.setMemberPrice(memberPrice.getPrice());
                            // 默认可以叠加其它优惠
                            memberPriceDTO.setAddOther(1);
                            memberPriceDTO.setSkuId(pmsSkuInfoEntity.getSkuId());
                            memberPriceFeignApi.save(memberPriceDTO);
                        }
                    }
                }
            }
        }


        // 6.4 批量保存 SKU 的优惠、满减等信息 gulimall_sms -> sms_sku_ladder & sms_sku_full_reduction & sms_member_price
    }

    @Override
    public void saveSpuInfo(PmsSpuInfoEntity infoEntity) {
        this.baseMapper.insert(infoEntity);
    }

    @Override
    public void saveSpuDescInfo(PmsSpuInfoDescEntity spuInfoDescEntity) {
        if (Objects.nonNull(spuInfoDescEntity)) {
            pmsSpuInfoDescDao.insert(spuInfoDescEntity);
        }
    }

    @Override
    public void saveSpuImages(List<PmsSpuImagesEntity> pmsSpuImagesEntities) {
        if (CollectionUtils.isNotEmpty(pmsSpuImagesEntities)) {
            pmsSpuImagesDao.insert(pmsSpuImagesEntities);
        }
    }

    @Override
    public void saveSpuBaseAttrs(List<PmsProductAttrValueEntity> pmsProductAttrValueEntities) {
        if (CollectionUtils.isNotEmpty(pmsProductAttrValueEntities)) {
            pmsProductAttrValueDao.insert(pmsProductAttrValueEntities);
        }
    }

}
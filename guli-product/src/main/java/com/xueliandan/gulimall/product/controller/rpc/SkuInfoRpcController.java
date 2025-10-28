package com.xueliandan.gulimall.product.controller.rpc;

import com.xueliandan.gulimall.common.utils.R;
import com.xueliandan.gulimall.product.api.dto.SkuInfoDTO;
import com.xueliandan.gulimall.product.entity.PmsSkuInfoEntity;
import com.xueliandan.gulimall.product.service.PmsSkuInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zxb 2025/10/21 20:16
 */
@RestController
@RequestMapping(path = "/product/sku/info")
public class SkuInfoRpcController {

    @Autowired
    private PmsSkuInfoService pmsSkuInfoService;


    @RequestMapping("/{skuId}")
    public R info(@PathVariable("skuId") Long skuId) {
        PmsSkuInfoEntity pmsSkuInfo = pmsSkuInfoService.getById(skuId);
        SkuInfoDTO skuInfoDTO = new SkuInfoDTO();
        BeanUtils.copyProperties(pmsSkuInfo, skuInfoDTO);
        return R.ok().put("data", skuInfoDTO);
    }

}

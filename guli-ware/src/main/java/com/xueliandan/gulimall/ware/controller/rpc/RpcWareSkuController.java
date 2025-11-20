package com.xueliandan.gulimall.ware.controller.rpc;

import com.xueliandan.gulimall.common.utils.R;
import com.xueliandan.gulimall.ware.service.WmsWareSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author zxb 2025/11/20 20:00
 */
@RestController
@RequestMapping(path = "/rpc/ware/sku")
public class RpcWareSkuController {

    @Autowired
    private WmsWareSkuService wmsWareSkuService;


    @RequestMapping("/hasStock")
    public Map<String, Boolean> skusHasStock(@RequestBody List<Long> skuIds) {
        return wmsWareSkuService.skusHasStock(skuIds);
    }

}

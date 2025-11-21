package com.xueliandan.gulimall.search.api.feign;

import com.xueliandan.gulimall.search.api.model.EsSkuModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.List;

/**
 * @author zxb 2025/11/21 19:13
 */
@FeignClient(name = "gulimall-search", contextId = "gulimall-search-EsModelSaveFeign")
public interface EsModelSaveFeign {

    @PostMapping(path = "/rpc/es/product/bulk-save")
   Boolean rpcBulkSaveProductModel(@RequestBody List<EsSkuModel> esSkuModels) throws IOException;

}

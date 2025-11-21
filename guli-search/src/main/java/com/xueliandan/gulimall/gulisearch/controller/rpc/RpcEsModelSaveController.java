package com.xueliandan.gulimall.gulisearch.controller.rpc;

import com.xueliandan.gulimall.gulisearch.service.IEsModelSaveService;
import com.xueliandan.gulimall.search.api.model.EsSkuModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author zxb 2025/11/21 19:11
 */
@RestController
@RequestMapping(path = "/rpc/es/")
public class RpcEsModelSaveController {

    @Resource
    private IEsModelSaveService esModelSaveService;

    @PostMapping(path = "/product/bulk-save")
    public Boolean rpcBulkSaveProductModel(@RequestBody List<EsSkuModel> esSkuModels) throws IOException {
        return esModelSaveService.bulkSaveProductModel(esSkuModels);
    }

}

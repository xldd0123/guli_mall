package com.xueliandan.gulimall.gulisearch.controller;

import com.xueliandan.gulimall.gulisearch.service.IEsModelSaveService;
import com.xueliandan.gulimall.search.api.model.EsSkuModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @author zxb 2025/11/20 20:57
 */
@RestController
@RequestMapping("/es")
public class EsModelSaveController {

    @Autowired
    private IEsModelSaveService esModelSaveService;

    @PostMapping(path = "/product/bulk-save")
    public Boolean bulkSaveProductModel(@RequestBody List<EsSkuModel> esSkuModels) throws IOException {
        return esModelSaveService.bulkSaveProductModel(esSkuModels);
    }

}

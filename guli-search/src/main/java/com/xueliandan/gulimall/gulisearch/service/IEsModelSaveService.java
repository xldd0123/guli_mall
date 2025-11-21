package com.xueliandan.gulimall.gulisearch.service;

import com.xueliandan.gulimall.search.api.model.EsSkuModel;

import java.io.IOException;
import java.util.List;

/**
 * @author zxb 2025/11/20 21:16
 */
public interface IEsModelSaveService {

    Boolean bulkSaveProductModel(List<EsSkuModel> esSkuModels) throws IOException;
}

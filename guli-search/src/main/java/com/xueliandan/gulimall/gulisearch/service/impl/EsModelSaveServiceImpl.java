package com.xueliandan.gulimall.gulisearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.xueliandan.gulimall.gulisearch.config.ElasticSearchConfig;
import com.xueliandan.gulimall.gulisearch.constant.EsConstant;
import com.xueliandan.gulimall.gulisearch.service.IEsModelSaveService;
import com.xueliandan.gulimall.search.api.model.EsSkuModel;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author zxb 2025/11/20 21:17
 */
@Service
public class EsModelSaveServiceImpl implements IEsModelSaveService {

    private final Logger log = LoggerFactory.getLogger(EsModelSaveServiceImpl.class);

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public Boolean bulkSaveProductModel(List<EsSkuModel> esSkuModels) throws IOException {
        if (CollectionUtils.isEmpty(esSkuModels)) return true;

        BulkRequest bulkRequest = new BulkRequest();

        for (EsSkuModel esSkuModel : esSkuModels) {
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(esSkuModel.getSkuId().toString());
            indexRequest.source(JSON.toJSONString(esSkuModel), XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, ElasticSearchConfig.COMMON_OPTIONS);

        // TODO 批量处理错误的请求
        boolean b = bulk.hasFailures();
        BulkItemResponse[] items = bulk.getItems();
        for (BulkItemResponse item : items) {
            if (item.isFailed()) {
                log.error("{} 商品上架错误!", item);
            }
        }
        return !b;
    }
}

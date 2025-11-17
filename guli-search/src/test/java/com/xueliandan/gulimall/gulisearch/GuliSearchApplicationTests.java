package com.xueliandan.gulimall.gulisearch;

import com.xueliandan.gulimall.gulisearch.config.ElasticSearchConfig;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class GuliSearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    @Test
    void contextLoads() {
        System.out.println(client);
    }

    /**
     * 存储索引
     */
    @Test
    void createIndex() {
        IndexRequest request = new IndexRequest("users");
        request.id("1");
        String jsonString = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";
        // 使用 json 一定要指定 XContentType.JSON
        request.source(jsonString, XContentType.JSON);

        try {
            IndexResponse indexResponse = client.index(request, ElasticSearchConfig.COMMON_OPTIONS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void compliantSearch() {
        SearchRequest searchRequest = new SearchRequest("bank");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(5);
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            System.out.println(hits.getTotalHits().value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

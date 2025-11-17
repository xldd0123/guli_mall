package com.xueliandan.gulimall.gulisearch.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zxb 2025/10/29 20:09
 */
@Configuration
public class ElasticSearchConfig {


    /**
     * 简而言之就是发送个 ES 服务端的请求选项，我们可以自定义请求参数，而不会改变 Elasticsearch 执行请求的方式
     * All APIs in the RestHighLevelClient accept a RequestOptions which you can use to customize the request
     * in ways that won’t change how Elasticsearch executes the request. For example, this is the place
     * where you’d specify a NodeSelector to control which node receives the request. See the low level client documentation for more examples of customizing the options.
     */
    public static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        // token 暂时没用到，先注掉。 这都是从官方使用文档里拷贝过来的
//        builder.addHeader("Authorization", "Bearer " + TOKEN);
//        builder.setHttpAsyncResponseConsumerFactory(
//                new HttpAsyncResponseConsumerFactory
//                        .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }

    @Bean
    public RestHighLevelClient esRestHighLevelClient() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("101.35.25.41", 9200, "http")));
        return client;
    }

}

package com.xueliandan.gulimall.gulisearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.xueliandan.gulimall.gulisearch.config.ElasticSearchConfig;
import com.xueliandan.gulimall.gulisearch.constant.EsConstant;
import com.xueliandan.gulimall.gulisearch.service.IMallSearchService;
import com.xueliandan.gulimall.gulisearch.vo.SearchParam;
import com.xueliandan.gulimall.gulisearch.vo.SearchResp;
import com.xueliandan.gulimall.search.api.model.EsSkuModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zxb 2026/2/3 15:03
 */
@Service
public class MallSearchServiceImpl implements IMallSearchService {

    @Resource
    private RestHighLevelClient client;


    @Override
    public SearchResp search(SearchParam searchParam) {
        SearchRequest searchRequest = buildSearchRequest(searchParam);
        SearchResp retVal;
        try {
            SearchResponse response = client.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
            retVal = buildSearchResponse(response, searchParam);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return retVal;
    }

    private SearchResp buildSearchResponse(SearchResponse response, SearchParam searchParam) {
        SearchResp retVal = new SearchResp();

        // 1. 获取商品列表
        SearchHits hits = response.getHits();
        List<EsSkuModel> products = new ArrayList<>();

        for (SearchHit hit : hits.getHits()) {
            // 存商品的时候，将 EsSkuModel 转换为 JSON 字符串，保存在 source 中，
            // 这里取的时候，将 JSON 字符串转换为 EsSkuModel 对象
            String sourceAsString = hit.getSourceAsString();
            EsSkuModel esSkuModel = JSON.parseObject(sourceAsString, EsSkuModel.class);

            // 处理高亮字段，searchRequest 对象中设置了高亮字段为 skuTitle，也就是只有传递了 skuTitle 字段，才会进行高亮
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField skuTitleHighlight = highlightFields.get("skuTitle");
            if (skuTitleHighlight != null) {
                String highLightValue = skuTitleHighlight.getFragments()[0].toString();
                esSkuModel.setSkuTitle(highLightValue);
            }

            products.add(esSkuModel);
        }
        retVal.setProducts(products);

        // 2. 设置分页信息
        long totalHits = hits.getTotalHits().value;
        retVal.setTotal(totalHits);
        retVal.setTotalPages((int) Math.ceil((double) totalHits / 20));
        retVal.setPageNum(searchParam.getPageNum());

        // 3. 获取聚合结果 - 品牌（brandId 是 Long 类型，使用 LongTerms）
//        LongTerms brandAgg = response.getAggregations().get("brand_agg");
        ParsedTerms brandAgg = response.getAggregations().get("brand_agg");
        List<SearchResp.BrandVO> brands = new ArrayList<>();
        if (brandAgg != null) {
            for (Terms.Bucket bucket : brandAgg.getBuckets()) {
                SearchResp.BrandVO brandVO = new SearchResp.BrandVO();
                brandVO.setBrandId((long) bucket.getKey());

                // 获取子聚合 - 品牌名称
                ParsedTerms brandNameAgg = bucket.getAggregations().get("brandName_agg");
                if (brandNameAgg != null && !brandNameAgg.getBuckets().isEmpty()) {
                    brandVO.setBrandName(brandNameAgg.getBuckets().get(0).getKeyAsString());
                }

                // 获取子聚合 - 品牌图片
                ParsedTerms brandImgAgg = bucket.getAggregations().get("brandImg_agg");
                if (brandImgAgg != null && !brandImgAgg.getBuckets().isEmpty()) {
                    brandVO.setBrandImg(brandImgAgg.getBuckets().get(0).getKeyAsString());
                }

                brands.add(brandVO);
            }
            retVal.setBrands(brands);
        }

        // 4. 获取聚合结果 - 分类（catalogId 是 Long 类型，使用 LongTerms）
        ParsedTerms catalogAgg = response.getAggregations().get("catalog_agg");
        List<SearchResp.CatelogVO> catelogs = new ArrayList<>();
        if (catalogAgg != null) {
            for (Terms.Bucket bucket : catalogAgg.getBuckets()) {
                SearchResp.CatelogVO catelogVO = new SearchResp.CatelogVO();
                catelogVO.setCatalogId((long) bucket.getKey());

                Terms catalogNameAgg = bucket.getAggregations().get("catalogName_agg");
                if (catalogNameAgg != null && !catalogNameAgg.getBuckets().isEmpty()) {
                    catelogVO.setCatalogName(catalogNameAgg.getBuckets().get(0).getKeyAsString());
                }

                catelogs.add(catelogVO);
            }
            retVal.setCatelogs(catelogs);
        }

        // 5. 获取聚合结果 - 属性（attrId 是 Long 类型，使用 LongTerms）
        Nested attrAgg = response.getAggregations().get("attr_agg");
        List<SearchResp.AttrVO> attrs = new ArrayList<>();
        if (attrAgg != null) {
            Terms attrIdAgg = attrAgg.getAggregations().get("attr_id_agg");
            if (attrIdAgg != null) {
                for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
                    SearchResp.AttrVO attrVO = new SearchResp.AttrVO();
                    attrVO.setAttrId((long) bucket.getKey());

                    Terms attrNameAgg = bucket.getAggregations().get("attr_name_agg");
                    if (attrNameAgg != null) {
                        attrVO.setAttrName(attrNameAgg.getBuckets().get(0).getKeyAsString());

                        List<String> attrValues = new ArrayList<>();
                        for (Terms.Bucket valueBucket : attrNameAgg.getBuckets()) {
                            attrValues.add(valueBucket.getKeyAsString());
                        }
                        attrVO.setAttrValue(attrValues);
                    }

                    attrs.add(attrVO);
                }
                retVal.setAttrs(attrs);
            }
        }

        return retVal;
    }

    // todo 测试各个查询参数是否生效
    private SearchRequest buildSearchRequest(SearchParam param) {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // =======================
        // 1. 构建 bool 查询
        // =======================
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // -------- must 模糊匹配--------
        if (!StringUtils.isEmpty(param.getKeyword())) {
            boolQuery.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
        }

        // -------- filter --------

        // 三级分类
        if (param.getCatalog3Id() != null) {
            boolQuery.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));
        }

        // 品牌
        if (param.getBrandId() != null && !param.getBrandId().isEmpty()) {
            // 多个匹配采用 terms 而非  term
            boolQuery.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
        }

        // 是否有库存
        if (param.getHasStock() != null) {
            boolQuery.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
        }

        // 价格区间 1_500,  skuPrice=_500,  skuPrice=1000_
        if (!StringUtils.isEmpty(param.getSkuPrice())) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");

            String[] prices = param.getSkuPrice().split("_");

            if (prices.length == 2) {

                if (!StringUtils.isEmpty(prices[0])) {
                    rangeQuery.gte(prices[0]);
                }
                if (!StringUtils.isEmpty(prices[1])) {
                    rangeQuery.lte(prices[1]);
                }
                boolQuery.filter(rangeQuery);
            }
            if (prices.length == 1 && param.getSkuPrice().startsWith("_")) {
                rangeQuery.gte(prices[0]);
                boolQuery.filter(rangeQuery);
            }
            if (prices.length == 1 && param.getSkuPrice().endsWith("_")) {
                rangeQuery.lte(prices[0]);
                boolQuery.filter(rangeQuery);
            }
        }

        // 属性筛选（nested）
        if (param.getAttrs() != null && !param.getAttrs().isEmpty()) {

            for (String attrStr : param.getAttrs()) {

                // attrs=1_3G:4G:5G,多个 attr 用数字标识，_1标识第一个属性，_2标识第二个属性，多个值用冒号分隔
                String[] attrSplit = attrStr.split("_");
                String attrId = attrSplit[0];
                String[] attrValues = attrSplit[1].split(":");

                BoolQueryBuilder nestedBool = QueryBuilders.boolQuery();
                nestedBool.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                nestedBool.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));

                // 每一个嵌套查询条件都用一个 bool，必须放到循环里
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery(
                        "attrs",
                        nestedBool,
                        ScoreMode.None
                );
                boolQuery.filter(nestedQuery);
            }
        }

        sourceBuilder.query(boolQuery);

        // =======================
        // 2. 排序
        // =======================
        if (!StringUtils.isEmpty(param.getSort())) {

            // saleCount_desc
            String[] sortSplit = param.getSort().split("_");
            String field = sortSplit[0];
            String order = sortSplit[1];

            sourceBuilder.sort(field,
                    order.equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC);
        }

        // =======================
        // 3. 分页
        // =======================
        int pageSize = 20;
        int from = (param.getPageNum() - 1) * pageSize;

        sourceBuilder.from(from);
        sourceBuilder.size(pageSize);

        // =======================
        // 4. 高亮，只有传了 keyword 查询参数才高亮
        // =======================
        if (StringUtils.isNotBlank(param.getKeyword())) {

            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<br style='color:red'>");
            highlightBuilder.postTags("<br>");

            sourceBuilder.highlighter(highlightBuilder);
        }

        // =======================
        // 5. 聚合
        // =======================

        // 品牌聚合
        TermsAggregationBuilder brandAgg =
                AggregationBuilders.terms("brand_agg")
                        .field("brandId")
                        .size(10);

        // 品牌聚合子聚合
        brandAgg.subAggregation(
                AggregationBuilders.terms("brandName_agg")
                        .field("brandName")
                        .size(10)
        );

        brandAgg.subAggregation(
                AggregationBuilders.terms("brandImg_agg")
                        .field("brandImg")
                        .size(10)
        );

        sourceBuilder.aggregation(brandAgg);

        // 分类聚合
        TermsAggregationBuilder catalogAgg =
                AggregationBuilders.terms("catalog_agg")
                        .field("catalogId")
                        .size(20);

        catalogAgg.subAggregation(
                AggregationBuilders.terms("catalogName_agg")
                        .field("catalogName")
                        .size(1) // 一个分类 ID 肯定只对一个分类名称，因此这里可以直接写 1
        );

        sourceBuilder.aggregation(catalogAgg);

        // 属性聚合（nested）
        NestedAggregationBuilder attrAgg =
                AggregationBuilders.nested("attr_agg", "attrs");

        TermsAggregationBuilder attrIdAgg =
                AggregationBuilders.terms("attr_id_agg")
                        .field("attrs.attrId")
                        .size(10);

        TermsAggregationBuilder attrNameAgg =
                AggregationBuilders.terms("attr_name_agg")
                        .field("attrs.attrName")
                        .size(10);

        TermsAggregationBuilder attrValueAgg =
                AggregationBuilders.terms("attr_value_agg")
                        .field("attrs.attrValue")
                        .size(10);

        attrNameAgg.subAggregation(attrValueAgg);
        attrIdAgg.subAggregation(attrNameAgg);
        attrAgg.subAggregation(attrIdAgg);

        sourceBuilder.aggregation(attrAgg);

        // =======================
        // 6. 构建 SearchRequest
        // =======================

        SearchRequest searchRequest =
                new SearchRequest(EsConstant.PRODUCT_INDEX);

        searchRequest.source(sourceBuilder);

        System.out.println("构建的DSL语句：" + sourceBuilder);

        return searchRequest;
    }
}

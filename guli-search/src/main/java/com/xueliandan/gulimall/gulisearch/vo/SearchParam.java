package com.xueliandan.gulimall.gulisearch.vo;

import lombok.Data;

import java.util.List;

/**
 * 商城首页检索条件分析
 *
 * @author zxb 2026/2/3 9:44
 */
@Data
public class SearchParam {


    private String keyword; // 关键字

    private Long catalog3Id; // 三级分类 ID

    private List<Long> brandId; // 品牌 ID，支持多选

    private String sort; // 支持的排序，譬如根据销量排序 saleCount_desc/asc


    /**
     * 还有很多查询条件，譬如 是否有货、价格区间等
     */
    private Integer hasStock;

    /**
     * skuPrice=1_500,  skuPrice=_500,  skuPrice=1000_
     */
    private String skuPrice;

    /**
     * 还可以根据规格属性或销售属性进行查询，属性太多了，这个约定，多个属性后面加上序号，且多个值之间用冒号分割
     * attrs=1_3G:4G:5G&attrs=2_骁龙845&attrs=4_高清屏
     */
    private List<String> attrs;

    private Integer pageNum; // 页码
}

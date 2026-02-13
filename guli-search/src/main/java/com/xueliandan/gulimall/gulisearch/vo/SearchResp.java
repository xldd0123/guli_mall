package com.xueliandan.gulimall.gulisearch.vo;

import com.xueliandan.gulimall.search.api.model.EsSkuModel;
import lombok.Data;

import java.util.List;

/**
 * @author zxb 2026/2/3 15:32
 */
@Data
public class SearchResp {

    private List<EsSkuModel> products; // 商品信息

    private Integer pageNum; // 当前页

    private Long total; // 总数

    private Integer totalPages; // 总页数

    // 查询结果涉及到的所有品牌
    private List<BrandVO> brands;

    // 查询结果涉及到的所有分类
    private List<CatelogVO> catelogs;

    // 查询结果涉及到的所有属性
    private List<AttrVO> attrs;

    /**
     * 品牌信息
     */
    @Data
    public static class BrandVO {
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    /**
     * 分类信息，分类和品牌是通用返回对象，不管是怎么查询，这两个属性总会存在
     * 分类要有分类 id 和分类的名称
     *
     */
    @Data
    public static class CatelogVO {
        private Long catalogId;
        private String catalogName;
    }

    /**
     * 所有商品共有的属性，可以提供查询，不能说给了一个查询不到的属性给到前端展示，这样单击这个属性就没数据了。
     */
    @Data
    public static class AttrVO {
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

}

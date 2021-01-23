package com.mrxiao.gulimall.gulimallseach.vo;

import com.mrxiao.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.List;

/**
 * @author Administrator
 * @ClassName SearchResponse
 * @Description 查询到的所以商品信息
 * @Version 1.0
 * @date 2020/11/17 0017 20:10
 */
@Data
public class SearchResult {
    private List<SkuEsModel> products;

    private Integer pageNum; //当前页码

    private Long total; //总数

    private Integer totalPages; //总页码

    private List<Integer> pageNavs;

    private List<BrandVo> brands; //当前查询到的结果，所以涉及到的品牌

    private List<CatalogVo> catalogvos;//当前查询到的结果，所以涉及到的分类

    private List<AttrdVo> attrdvos;//当前查询到的结果，所以涉及到的属性

    /** 以上是返回给页面的所以数据**/

    @Data
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class CatalogVo{
        private Long catalogId;
        private String catalogName;
    }


    @Data
    public static class AttrdVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }
}

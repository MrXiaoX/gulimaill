package com.mrxiao.gulimall.product.vo;

import com.mrxiao.gulimall.product.entity.SkuImagesEntity;
import com.mrxiao.gulimall.product.entity.SkuInfoEntity;
import com.mrxiao.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * @author Administrator
 * @ClassName SkuItemVo
 * @Description TODO
 * @Version 1.0
 * @date 2020/12/14 0014 19:42
 */
@Data
public class SkuItemVo {

    //1.sku基本信息
    SkuInfoEntity info;


    //2.sku图片信息
    List<SkuImagesEntity> images;

    //3.获取spu的销售属性组合
    List<SkuItemSaleAttrVo> saleAttr;

    //4.获取spu的介绍
    SpuInfoDescEntity desc;

    //5.获取spu规格参数信息

    List<SkuItemAttrGroupVo> groupAttrs;

    @Data
    public static class SkuItemSaleAttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValues;
    }

    @Data
    public static class SkuItemAttrGroupVo{
        private String groupName;
        private List<SpuBaseAttrVo> attrs;
    }


    @Data
    public static class SpuBaseAttrVo{
        private String attrName;
        private String attrValue;
    }
}

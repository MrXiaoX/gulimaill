package com.mrxiao.gulimall.gulimallseach.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Administrator
 * @ClassName SearchParam
 * @Description 封装页面所以可能传过来的请求参数
 * @Version 1.0
 * @date 2020/11/17 0017 19:46
 */
@Data
public class SearchParam {

    private String keyword; //页面传过来的全文匹配关键字
    private Long  catalog3Id; //三级分类Id

    private String sort;

    private Integer hasStock; //是否显示有货
    private String skuPrice; //价格区间查询
    private List<Long> brandId; //按照品牌进行查询，可多选
    private List<String> attrs; //按照属性进行筛选 //attrs  8_白色
    private Integer pageNum=1; //页码
}

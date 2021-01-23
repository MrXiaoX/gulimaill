package com.mrxiao.gulimall.gulimallseach.service.impl;

import com.alibaba.fastjson.JSON;
import com.mrxiao.common.to.es.SkuEsModel;
import com.mrxiao.gulimall.gulimallseach.config.GulimallElasticsearchConfig;
import com.mrxiao.gulimall.gulimallseach.constant.EsConstant;
import com.mrxiao.gulimall.gulimallseach.service.MaillSearchService;
import com.mrxiao.gulimall.gulimallseach.vo.SearchParam;
import com.mrxiao.gulimall.gulimallseach.vo.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Administrator
 * @ClassName SearchControllerImpl
 * @Description TODO
 * @Version 1.0
 * @date 2020/11/17 0017 20:00
 */
@Service
public class MaillSearchServiceImpl implements MaillSearchService {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public SearchResult search(SearchParam searchParam) {
        //1.动态构建出查询需要的DSL语句
        SearchResult result=null;
        //2、准备检索请求
        SearchRequest searchRequest =buildSearchRequest(searchParam);

        try {
            //3、执行检索请求
            SearchResponse respones = client.search(searchRequest, GulimallElasticsearchConfig.COMMON_OPTIONS);

            //4、分析响应数据封装成我们需要的格式
            result=buildSearchResult(respones,searchParam);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * @Author mrxiao
     * @Description //准备检索请求
     *  #模糊匹配,过滤(按照属性，分类，品牌，价格区间，库存)，排序，分页，高亮，聚合分析
     * @Date 19:34 2020/11/30 0030
     * @Version 1.0
     * @Param org.elasticsearch.action.search.SearchRequest
     * @return org.elasticsearch.action.search.SearchRequest
     **/
    private SearchRequest buildSearchRequest(SearchParam searchParam) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        /**
         * 查询 模糊匹配,过滤(按照属性，分类，品牌，价格区间，库存)
         */
        //1.构建bool query
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //1.1 must-模糊匹配
        if(StringUtils.isNotBlank(searchParam.getKeyword())){
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle",searchParam.getKeyword()));
        }

        //1.2 bool-filter 按照三级分类id查询
        if(searchParam.getCatalog3Id()!=null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId",searchParam.getCatalog3Id()));
        }

        //1.2 bool-filter 按照品牌id查询
        if(!CollectionUtils.isEmpty(searchParam.getBrandId())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("brandId",searchParam.getBrandId()));
        }

        //1.2 bool-filter 按照所有指定的属性进行查询
        if(!CollectionUtils.isEmpty(searchParam.getAttrs())){
            for (String attr : searchParam.getAttrs()) {
                BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                String[] s = attr.split("_");
                String attrId= s[0]; //检索属性id
                String[] attrValues = s[1].split(":");
                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrValue",attrValues));
                //每个必须生成一个nested查询
                QueryBuilders.nestedQuery("attrs",nestedBoolQuery, ScoreMode.None);
                boolQueryBuilder.filter(nestedBoolQuery);
            }
        }

        //1.2 bool-filter -按照库存是否有进行查询
        if(searchParam.getHasStock()!=null){

            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock",searchParam.getHasStock()==1));
        }

        //1.2 bool-filter -按照价格区间
        if(StringUtils.isNotBlank(searchParam.getSkuPrice())){
            //
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String[] split = searchParam.getSkuPrice().split(",");
            if(split.length==2){
                //区间
                rangeQuery.gte(split[0]).lte(split[1]);
            }else if(split.length==1){
                if(searchParam.getSkuPrice().startsWith("_")){
                    rangeQuery.lte(split[0]);
                }
                if(searchParam.getSkuPrice().endsWith("_")){
                    rangeQuery.gte(split[0]);
                }
            }
            boolQueryBuilder.filter(rangeQuery);
        }

        //把以前的所有条件都拿来封装
        sourceBuilder.query(boolQueryBuilder);
        /**
         * 排序，分页，高亮
         */
        //2.1 排序
        if(StringUtils.isNotBlank(searchParam.getSort())){
            String sort = searchParam.getSort();
            String[] s = sort.split("_");
            SortOrder sortOrder=s[1].equalsIgnoreCase("asc")?SortOrder.ASC:SortOrder.DESC;
            sourceBuilder.sort(s[0],sortOrder);
        }


        //2.2 分页
        sourceBuilder.from((searchParam.getPageNum()-1)*EsConstant.PRODUCT_PAGESIZE);
        sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);

        //2.3 高亮
        if(StringUtils.isNotBlank(searchParam.getKeyword())){
            HighlightBuilder builder = new HighlightBuilder();
            builder.field("skuTitle");
            builder.preTags("<b style='color:red'>");
            builder.postTags("</b>");
            sourceBuilder.highlighter(builder);
        }
        /**
         * 聚合分析
         */
        // 1.品牌分析
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brand_agg");
        brandAgg.field("brandId").size(50);

        //品牌聚合子聚合
        brandAgg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brandAgg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        sourceBuilder.aggregation(brandAgg);

        //2.分类聚合
        TermsAggregationBuilder catalogAgg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(20);
        catalogAgg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));
        sourceBuilder.aggregation(catalogAgg);

        //3.属性聚合
        NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attr_agg", "attrs");

        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        //聚合分析出attrId对应的名字
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        //聚合分析出attrId对应的所有可能值
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));

        attrAgg.subAggregation(attrIdAgg);
        sourceBuilder.aggregation(attrAgg);

        String dsl = sourceBuilder.toString();
        System.out.println("构建的DSL>>>:"+dsl);
        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
        return searchRequest;
    }

    /**
     * @Author mrxiao
     * @Description //准备检索响应
     * @Date 19:34 2020/11/30 0030  
     * @Version 1.0
     * @Param com.mrxiao.gulimall.gulimallseach.vo.SearchResult
     * @return com.mrxiao.gulimall.gulimallseach.vo.SearchResult
     **/
    private SearchResult buildSearchResult(SearchResponse respones,SearchParam searchParam) {
        SearchResult result = new SearchResult();

        //1.返回所有查询到的商品
        SearchHits hits = respones.getHits();
        List<SkuEsModel> esModelList=new ArrayList<>();
        if( hits.getHits()!=null&& hits.getHits().length>0){
            SearchHit[] hits1 = hits.getHits();
            for (SearchHit hit : hits1) {
                String sourceAsString = hit.getSourceAsString();
                SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                if(StringUtils.isNotBlank(searchParam.getKeyword())){
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String skuTitleStr = skuTitle.getFragments()[0].string();
                    esModel.setSkuTitle(skuTitleStr);
                }
                esModelList.add(esModel);
            }
        }
        result.setProducts(esModelList);
        //2.返回所有商品涉及到的所有属性信息
        List<SearchResult.AttrdVo> attrdVos=new ArrayList<>();
        ParsedNested attrAgg = respones.getAggregations().get("attr_agg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            SearchResult.AttrdVo attrdVo = new SearchResult.AttrdVo();
            //1.得到attrId
            long attrId = bucket.getKeyAsNumber().longValue();
            //2.得到attrName
            String attrName =((ParsedStringTerms)bucket.getAggregations().get("attr_name_agg")).getBuckets().get(0).getKeyAsString();
            //3.得到属性所有值
            List<String> attrValues= ((ParsedStringTerms) bucket.getAggregations().get("attr_value_agg")).getBuckets().stream().map(item -> {
                String keyAsString = ((Terms.Bucket) item).getKeyAsString();
                return keyAsString;
            }).collect(Collectors.toList());

            attrdVo.setAttrId(attrId);
            attrdVo.setAttrName(attrName);
            attrdVo.setAttrValue(attrValues);
            attrdVos.add(attrdVo);
        }
        result.setAttrdvos(attrdVos);
        //3.返回所有商品涉及到的所有品牌信息
        ParsedLongTerms brandAgg = respones.getAggregations().get("brand_agg");
        List<SearchResult.BrandVo> brandVos=new ArrayList<>();
        List<? extends Terms.Bucket> buckets1 = brandAgg.getBuckets();
        for (Terms.Bucket bucket : buckets1) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            //1、得到品牌id
            long brandId = bucket.getKeyAsNumber().longValue();
            //2、得到品牌名
            String brandName =((ParsedStringTerms)bucket.getAggregations().get("brand_name_agg")).getBuckets().get(0).getKeyAsString();
            //3、得到品牌图片
            String brandImg =((ParsedStringTerms)bucket.getAggregations().get("brand_img_agg")).getBuckets().get(0).getKeyAsString();
            brandVo.setBrandId(brandId);
            brandVo.setBrandName(brandName);
            brandVo.setBrandImg(brandImg);
            brandVos.add(brandVo);
        }

        result.setBrands(brandVos);
        //4.返回所有商品涉及到的所有分类信息
        ParsedLongTerms catalogAgg = respones.getAggregations().get("catalog_agg");
        List<SearchResult.CatalogVo> catalogVos=new ArrayList<>();
        List<? extends Terms.Bucket> buckets = catalogAgg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            //得到分类id
            String keyAsString = bucket.getKeyAsString();
            catalogVo.setCatalogId(Long.parseLong(keyAsString));

            //得到分类名
            ParsedStringTerms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
            String catalog_name = catalog_name_agg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalog_name);
            catalogVos.add(catalogVo);
        }
        result.setCatalogvos(catalogVos);
//        =========以上从聚合结果总获取========
        //5、分页信息-页码
        result.setPageNum(searchParam.getPageNum());
        //5、分页信息-总记录数
        long total = hits.getTotalHits().value;
        result.setTotal(total);
        //5、分页信息-总页码
        int totalPages=(int)total%EsConstant.PRODUCT_PAGESIZE==0?(int)total/EsConstant.PRODUCT_PAGESIZE:((int)total/EsConstant.PRODUCT_PAGESIZE+1);
        result.setTotalPages(totalPages);

        ArrayList<Integer> pageNavs = new ArrayList<>();
        for (int i = 0; i < totalPages; i++) {
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);
        return result;
    }


}

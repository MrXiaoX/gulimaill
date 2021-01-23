package com.mrxiao.gulimall.gulimallseach;

import com.alibaba.fastjson.JSON;
import com.mrxiao.gulimall.gulimallseach.config.GulimallElasticsearchConfig;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
class GulimallSeachApplicationTests {

    @Autowired
    public  RestHighLevelClient restHighLevelClient;


    @ToString
    @Data
    static class Account{

        /**
         * Auto-generated: 2020-08-20 19:26:20
         *
         * @author bejson.com (i@bejson.com)
         * @website http://www.bejson.com/java2pojo/
         */
        public class JsonRootBean {

            private int account_number;
            private int balance;
            private String firstname;
            private String lastname;
            private int age;
            private String gender;
            private String address;
            private String employer;
            private String email;
            private String city;
            private String state;


        }
    }

    /**
     * 测试es的存储数据
     */
    @Test
    void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("2");
//        indexRequest.source("userName","深大","age","18");
        Users users = new Users("深大","18",2);
        String jsonString= JSON.toJSONString(users);
        indexRequest.source(jsonString, XContentType.JSON);
        //执行操作
        IndexResponse index = restHighLevelClient.index(indexRequest, GulimallElasticsearchConfig.COMMON_OPTIONS);
        //提取有用的响应数据
        System.out.println(index);
    }

    @Test
    public void searchData() throws IOException {
        //1.创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        //指定索引
        searchRequest.indices("bank");
        //指定DSL,检索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //1.1构造检索条件
//        searchSourceBuilder.query();balance
//        searchSourceBuilder.from();
//        searchSourceBuilder.size();
//        searchSourceBuilder.aggregation();
        searchSourceBuilder.query(QueryBuilders.matchQuery("address","mill"));

        //1.2按照年龄值分布进行聚合
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        searchSourceBuilder.aggregation(ageAgg);

        //1.3计算平均薪资
        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
        searchSourceBuilder.aggregation(balanceAvg);
//        System.out.println("searchSourceBuilder:"+searchSourceBuilder.toString());

        //2.执行检索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, GulimallElasticsearchConfig.COMMON_OPTIONS);
//        System.out.println(searchResponse.toString());

        //3.分析结果
        SearchHits hits = searchResponse.getHits();
        //3.1获取到查询的所有结果
        SearchHit[] searchHit = hits.getHits();
        for (SearchHit documentFields : searchHit) {
            String sourceAsString = documentFields.getSourceAsString();
            System.out.println("sourceAsString:"+sourceAsString);
            Account account = JSON.parseObject(sourceAsString, Account.class);
            System.out.println("account:"+account);
        }
        //3.2获取这次检索到的分析信息
        Aggregations aggregations = searchResponse.getAggregations();
        System.out.println(aggregations);
//        for (Aggregation aggregation : aggregations.asList()) {
//            System.out.println("当前聚合"+aggregation.getName());
//        }

        Terms ageAgg1 = aggregations.get("ageAgg");
        for (Terms.Bucket bucket: ageAgg1.getBuckets()) {
            String keyAsString = bucket.getKeyAsString();
            System.out.println("年龄"+keyAsString);
        }
        Avg avg = aggregations.get("avg");
        System.out.println("平均薪资"+avg.getValue());
    }

    @Data
    class Users{
        private String name;
        private String gender;
        private Integer age;

        public Users(String name, String gender, Integer age) {
            this.name = name;
            this.gender = gender;
            this.age = age;
        }
    }

    @Test
    void contextLoads() {

        System.out.println(restHighLevelClient);
    }

}

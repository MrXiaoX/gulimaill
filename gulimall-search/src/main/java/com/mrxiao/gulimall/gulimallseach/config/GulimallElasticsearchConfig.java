package com.mrxiao.gulimall.gulimallseach.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 导入依赖
 */
@Configuration
public class GulimallElasticsearchConfig {

    public static final RequestOptions COMMON_OPTIONS;
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
//        builder.addHeader("Authorization", "Bearer " + TOKEN);
//        builder.setHttpAsyncResponseConsumerFactory(
//                new HttpAsyncResponseConsumerFactory
//                        .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }

    @Bean
    public RestHighLevelClient esRestClient(){
        RestClientBuilder builder=null;
        builder=RestClient.builder(new HttpHost("172.0.9.109",9200,"http"));
        RestHighLevelClient client=new RestHighLevelClient(builder);
        return client;
    }
}

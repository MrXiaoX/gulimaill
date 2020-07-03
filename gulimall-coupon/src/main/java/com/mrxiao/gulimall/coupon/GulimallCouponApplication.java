package com.mrxiao.gulimall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 1.如何使用nacos作为配置中心统一管理配置
 *   1) 引入相关依赖
 *   <dependency>
 *      <groupId>com.alibaba.cloud</groupId>
 *      <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
 *  </dependency>
 *  2) 创建一个 bootstrap.properties 配置:
 *  spring.application.name=gulimall-coupon
 *  spring.cloud.nacos.config.server-addr=127.0.0.1:8848
 *  3) 需要给配置中心默认添加一个数据集 gulimall-coupon.properties 默认规则项目名.properties
 *  4) 给项目名.properties 添加相关配置
 *  5) 动态获取配置 @RefreshScope @Value  如果配置中心和当前应用中都配置了相同项优先使用配置中心的配置
 *
 * 2.配置中心 细节
 *  1) 命名空间:配置geli
 *     默认: public(保留空间)默认新增的所有配置都在public空间下
 *      1、开发、测试、生产:利用命名空间来做环境隔离
 *        注意需要在bootstrap.properties 配置中 修改需要的命名空间id
 *        spring.cloud.nacos.config.namespace=56286062-0e58-4bd0-99e2-3914fd50a86f
 *      2、每一个微服务之间互相隔离配置，每一个微服务都创建一个自己的命名空间，只加载自己命名空间下的配置
 *  2) 配置集:所有配置集合
 *  3) 配置集Id:类似配置文件名
 *     Data ID
 *  4) 配置分组：
 *    默认所以配置集都属于: DEFAULT_GROUP
 *
 *  每个微服务创建自己的命名空间，使用配置分组区分环境，dev，test，prod
 *
 * 3.同事加载多个配置集
 *  1) 微服务任何配置信息，任何配置文件都可以放在配置中心中
 *  2) 只需要在 bootstrap.properties 说明加载配置中心那行配置即可
 *  3) @Value @Configruntion
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GulimallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallCouponApplication.class, args);
    }

}

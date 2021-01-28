package com.mrxiao.gulimall.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 使用RabbitMq
 *  1、引入amqp场景; RabbitAutoConfiguration就会自动生效
 *  2、给容器中自动配置了
 *    RabbitTemplate、AmqpAdmin、CachingConnectionFactory、RabbitMessagingTemplate
 *    所以属性都是 spring.rabbitmq
 *  3、给配置文件中配置 spring.rabbitmq信息
 *  4、@EnableRabbit开启功能
 *  5、监听消息@RabbitListener，前提必须要@EnableRabbit
 *    @RabbitListener:类+方法(监听队列即可)
 *    @RabbitHandler:方法(重载区分不同的消息)
 *
 */
@EnableRabbit
@SpringBootApplication
public class GulimallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }

}

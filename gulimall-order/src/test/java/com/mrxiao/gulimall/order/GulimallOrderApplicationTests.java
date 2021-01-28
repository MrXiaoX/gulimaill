package com.mrxiao.gulimall.order;

import com.alibaba.fastjson.JSON;
import com.mrxiao.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@Slf4j
//@RunWith(SpringRunner.class)
@SpringBootTest
class GulimallOrderApplicationTests {


    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void sendMessageTest() {
        OrderReturnReasonEntity entity = new OrderReturnReasonEntity();
        entity.setName("张三");
        entity.setCreateTime(new Date());
        entity.setId(12l);
        rabbitTemplate.convertAndSend("hello-exchange","hello.java",entity);
        log.info("消息发送完成{}", JSON.toJSONString(entity));
    }

    /**
     *
     *
     */
    @Test
    void createExchange() {

        /*
         String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
         */
        DirectExchange directExchange = new DirectExchange("hello-exchange",true,false);
        amqpAdmin.declareExchange(directExchange);
        log.info("exchange[{}] 创建成功",directExchange.getName());
    }


    @Test
    void createQueue() {
        //String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        Queue queue = new Queue("hello-queue",true,false,false);
        amqpAdmin.declareQueue(queue);
        log.info("Queue[{}] 创建成功",queue.getName());
    }

    @Test
    void createBinding() {
        //String destination, [目的地]
        // DestinationType destinationType,  [目的地类型]
        // String exchange, String routingKey,  [录音键]
        //			@Nullable Map<String, Object> arguments  [自定义参数]
        Binding binding = new Binding("hello-queue",
                Binding.DestinationType.QUEUE,
                "hello-exchange",
                "hello.java",null);
        amqpAdmin.declareBinding(binding);
        log.info("binding[{}] 创建成功","hello-binding");
    }
}

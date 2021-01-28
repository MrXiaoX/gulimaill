package com.mrxiao.gulimall.order.controller;

import com.mrxiao.gulimall.order.entity.OrderEntity;
import com.mrxiao.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

/**
 * @author Administrator
 * @ClassName RabbitController
 * @Description TODO
 * @Version 1.0
 * @date 2021/1/23 0023 17:32
 */
@RestController
@Slf4j
@RequestMapping("/rabbit")
public class RabbitController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMq")
    public String sendMessage(@RequestParam(value = "num",defaultValue = "10") Integer num) {

        for (int i=0;i<num;i++){
            if(i%2==0){
                OrderReturnReasonEntity entity = new OrderReturnReasonEntity();
                entity.setName("张三");
                entity.setCreateTime(new Date());
                entity.setId(12l);
                rabbitTemplate.convertAndSend("hello-exchange","hello.java",entity,new CorrelationData(UUID.randomUUID().toString()));
            }else{
                OrderEntity entity = new OrderEntity();
                entity.setOrderSn(UUID.randomUUID().toString());
                rabbitTemplate.convertAndSend("hello-exchange","hello.java",entity,new CorrelationData(UUID.randomUUID().toString()));
            }
            log.info("消息发送完成");
        }


        return "发送成功";
    }
}

package com.mrxiao.gulimall.order;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
//@RunWith(SpringRunner.class)
@SpringBootTest
class GulimallOrderApplicationTests {


    @Autowired
    AmqpAdmin amqpAdmin;

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
        log.info("exchange 创建成功");
    }
}

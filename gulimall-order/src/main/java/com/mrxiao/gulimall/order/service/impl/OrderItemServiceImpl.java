package com.mrxiao.gulimall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrxiao.common.utils.PageUtils;
import com.mrxiao.common.utils.Query;
import com.mrxiao.gulimall.order.dao.OrderItemDao;
import com.mrxiao.gulimall.order.entity.OrderEntity;
import com.mrxiao.gulimall.order.entity.OrderItemEntity;
import com.mrxiao.gulimall.order.entity.OrderReturnReasonEntity;
import com.mrxiao.gulimall.order.service.OrderItemService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;


@RabbitListener(queues = {"hello-queue"})
@Service("orderItemService")
@Slf4j
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }



    /**
     * 声明需要监听所有队列消息
     * 参数可以写以下类型
     * 1、Message message 原始消息，头+体
     * 2、T<发送消息实体> OrderReturnReasonEntity content
     * 3、Channel channel:当前传输数据通道
     *
     *  queue:可以很多人来监听。只要收到消息，队列删除消息，而且只能有一个收到此消息
     *   创建:
     *    1) 订单服务启动多个，同一个消息，只能有一个接收
     *    2) 只有一个处理完，方法运行结束，才接收一个消息
     *
     * @param message
     */
//    @RabbitListener(queues = {"hello-queue"})
    @RabbitHandler
    public void recieveMessage(Message message, OrderReturnReasonEntity content, Channel channel){
        System.out.println("接收消息...内容:"+content);
        byte[] body = message.getBody();
        //消息头属性信息
        MessageProperties messageProperties = message.getMessageProperties();
        System.out.println("消息处理完成"+content.getName());
        //channel内顺序自增长
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        System.out.println("deliveryTag>>"+deliveryTag);
        //签收货物,非批量模式
        try {
            if(deliveryTag%2==0){
                System.out.println("货物签收"+deliveryTag);
                channel.basicAck(deliveryTag,false);
            }else{
                //long deliveryTag 分发代理
                //boolean multiple 丢弃
                //boolean requeue 发回服务器，服务器重新入队
                channel.basicNack(deliveryTag,false,false);
                System.out.println("货物未签收"+deliveryTag);
            }
        } catch (IOException e) {
            //网络中断
            e.printStackTrace();
        }

    }

    @RabbitHandler
    public void recieveMessageTwo(Message message, OrderEntity content, Channel channel){
        System.out.println("接收消息...内容:"+content);
        byte[] body = message.getBody();

        MessageProperties messageProperties = message.getMessageProperties();

    }
}
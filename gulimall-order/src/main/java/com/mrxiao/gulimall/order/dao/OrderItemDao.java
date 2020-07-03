package com.mrxiao.gulimall.order.dao;

import com.mrxiao.gulimall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author mrxiao
 * @email ismrxiao@163.com
 * @date 2020-05-13 19:38:27
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}

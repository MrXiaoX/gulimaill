package com.mrxiao.gulimall.coupon.dao;

import com.mrxiao.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author mrxiao
 * @email ismrxiao@163.com
 * @date 2020-05-13 18:54:29
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}

package com.mrxiao.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mrxiao.common.utils.PageUtils;
import com.mrxiao.gulimall.coupon.entity.SpuBoundsEntity;

import java.util.Map;

/**
 * 商品spu积分设置
 *
 * @author mrxiao
 * @email ismrxiao@163.com
 * @date 2020-05-13 18:54:29
 */
public interface SpuBoundsService extends IService<SpuBoundsEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


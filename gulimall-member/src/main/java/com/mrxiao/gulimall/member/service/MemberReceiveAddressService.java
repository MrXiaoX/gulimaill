package com.mrxiao.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mrxiao.common.utils.PageUtils;
import com.mrxiao.gulimall.member.entity.MemberReceiveAddressEntity;

import java.util.Map;

/**
 * 会员收货地址
 *
 * @author mrxiao
 * @email ismrxiao@163.com
 * @date 2020-05-13 19:31:55
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


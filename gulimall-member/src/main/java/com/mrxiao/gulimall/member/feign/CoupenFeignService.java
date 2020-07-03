package com.mrxiao.gulimall.member.feign;

import com.mrxiao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 这是一个声明式的远程调用
 */
@FeignClient("gulimall-coupon")
public interface CoupenFeignService {

    @RequestMapping("/coupon/coupon/member/list")
    R memerCoupon();
}

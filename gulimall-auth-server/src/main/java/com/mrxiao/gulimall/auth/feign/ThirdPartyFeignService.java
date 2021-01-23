package com.mrxiao.gulimall.auth.feign;

import com.mrxiao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Administrator
 * @ClassName ThirdPartyFeignService
 * @Description TODO
 * @Version 1.0
 * @date 2020/12/15 0015 20:47
 */
@FeignClient("gulimall-third-party")
public interface ThirdPartyFeignService {

    @GetMapping("/sms/sendcode")
    R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}

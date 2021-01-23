package com.mrxiao.gulimall.thirdparty.controller;

import com.mrxiao.common.utils.R;
import com.mrxiao.gulimall.thirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Administrator
 * @ClassName SmsSendController
 * @Description TODO
 * @Version 1.0
 * @date 2020/12/15 0015 20:42
 */
@RestController
@RequestMapping("/sms")
public class SmsSendController {

    @Autowired
    SmsComponent smsComponent;


    /**
     * @Author mrxiao
     * @Description //发送短信验证码接口
     * @Date 20:44 2020/12/15 0015
     * @Version 1.0
     * @Param com.mrxiao.common.utils.R
     * @return com.mrxiao.common.utils.R
     **/
    @GetMapping("/sendcode")
    public R sendCode(@RequestParam("phone") String phone,@RequestParam("code") String code){
        smsComponent.sendSms(phone,code);
        return R.ok();
    }

}

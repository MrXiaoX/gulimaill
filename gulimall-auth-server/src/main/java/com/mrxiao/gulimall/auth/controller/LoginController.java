package com.mrxiao.gulimall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.mrxiao.common.constant.AuthServerConstant;
import com.mrxiao.common.exception.BizCodeEnume;
import com.mrxiao.common.utils.R;
import com.mrxiao.common.vo.MemberResponsVo;
import com.mrxiao.gulimall.auth.feign.MemberService;
import com.mrxiao.gulimall.auth.feign.ThirdPartyFeignService;
import com.mrxiao.gulimall.auth.vo.UserLoginVo;
import com.mrxiao.gulimall.auth.vo.UserRegistVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @ClassName LoginController
 * @Description TODO
 * @Version 1.0
 * @date 2020/12/15 0015 18:47
 */
@Controller
@Slf4j
public class LoginController {


    @Autowired
    ThirdPartyFeignService thirdPartyFeignService;


    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    MemberService memberService;

    @ResponseBody
    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone){
        //1.接口防刷

        //2.验证码的再次校验，redis，存key-phone,value-code  sms:code:
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if(StringUtils.isNotBlank(redisCode)){
            long parseLong = Long.parseLong(redisCode.split("_")[1]);
            if(System.currentTimeMillis()-parseLong<60000){
                //60秒内不能再发
                return R.error(BizCodeEnume.SMS_CODE_EXCEPTION.getCode(),BizCodeEnume.SMS_CODE_EXCEPTION.getMsg());
            }
        }

        String code = UUID.randomUUID().toString().substring(0, 5);
        String substring = code+"_"+System.currentTimeMillis();
        //redis缓存验证码，防止同一个phone在60秒内的再次发送验证码
        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX+phone,substring,10, TimeUnit.MINUTES);

        log.info("参数:{}",phone);
        thirdPartyFeignService.sendCode(phone,code);
        return R.ok();
    }


    /**
     * @Author mrxiao
     * TODO 重定向携带数据，利用session原理，将数据存储到session中
     *   只要跳到下一个页面去除session值之后，session里面的数据就会删除
     * TODO 1、分布式session问题
     * @Description redirectAttributes:模拟重定向数据的
     * @Date 19:24 2020/12/17 0017
     * @Version 1.0
     * @Param java.lang.String
     * @return java.lang.String
     **/
    @PostMapping("/regist")
    public String regist(@Valid UserRegistVo userRegistVo, BindingResult result, RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap
                    (FieldError::getField, FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errors",errors);
            //校验出错，转发到注册页
            return "redirect:http://auth.gulimall.com/reg.html";
        }
        //1、校验验证码
        String code = userRegistVo.getCode();

        String s = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + userRegistVo.getPhone());
        if(StringUtils.isNotBlank(s)){
            if(code.equals(s.split("_")[0])){
                redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX+userRegistVo.getPhone());
                R r=memberService.regist(userRegistVo);
                if(r.getCode()==0){

                    return "redirect:http://auth.gulimall.com/login.html";
                }else{
                    Map<String,String> error=new HashMap<>();
                    error.put("msg",r.getData("msg",new TypeReference<String>(){}).toString());
//                    error.put("msg",r.getData().toString());
                    redirectAttributes.addFlashAttribute("errors",error);
                    //注册成功回到首页。回到登录页
                    return "redirect:http://auth.gulimall.com/reg.html";
                }

            }else {
                Map<String,Object> error=new HashMap<>();
                error.put("code","验证码错误");
                redirectAttributes.addFlashAttribute("errors",error);
                return "redirect:http://auth.gulimall.com/reg.html";
            }
        }else{
            Map<String,Object> error=new HashMap<>();
            error.put("code","验证码错误");
            redirectAttributes.addFlashAttribute("errors",error);
            return "redirect:http://auth.gulimall.com/reg.html";
        }

    }


    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes redirectAttributes){
        R login = memberService.login(vo);
        if(login.getCode()==0){
            //远程登录
            MemberResponsVo data = (MemberResponsVo) login.getData("data", new TypeReference<MemberResponsVo>() {});
            return "redirect:http://gulimall.com";
        }else{
            Map<String,Object> error=new HashMap<>();
                error.put("msg",login.getData("msg",new TypeReference<String>(){}));
            redirectAttributes.addFlashAttribute("errors",error);
            return "redirect:http://auth.gulimall.com/reg.html";
        }

    }
}

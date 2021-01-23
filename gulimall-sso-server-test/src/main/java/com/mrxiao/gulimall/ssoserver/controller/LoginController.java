package com.mrxiao.gulimall.ssoserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author Administrator
 * @ClassName LoginController
 * @Description TODO
 * @Version 1.0
 * @date 2021/1/18 0018 10:28
 */
@Controller
public class LoginController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/userInfo")
    @ResponseBody
    public String userInfo(@RequestParam(value = "token") String token){
        String value = redisTemplate.opsForValue().get(token);
        return value;
    }

    @GetMapping("/login.html")
    public String loginPage(@RequestParam(value = "redirect_url",required = false) String url, Model model,
    @CookieValue(value ="sso_token",required = false) String token){
        if(!StringUtils.isEmpty(token)){
            return "redirect:"+url+"?token="+token;
        }
        model.addAttribute("url",url);
        return "login";
    }

    @PostMapping("/doLogin")
    public String doLogin(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("url") String url, HttpServletResponse response){
        if(!StringUtils.isEmpty(username)&&!StringUtils.isEmpty(password)){

            String uuid = UUID.randomUUID().toString().replace("-","");
            redisTemplate.opsForValue().set(uuid,username);
            Cookie cookie = new Cookie("sso_token",uuid);
            response.addCookie(cookie);
            return "redirect:"+url+"?token="+uuid;
        }

        //登录成功跳转,跳回到之前的页面
        return "";
    }
}

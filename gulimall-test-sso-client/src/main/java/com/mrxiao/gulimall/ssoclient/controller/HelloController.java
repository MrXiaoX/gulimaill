package com.mrxiao.gulimall.ssoclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @ClassName HelloController
 * @Description TODO
 * @Version 1.0
 * @date 2021/1/18 0018 10:08
 */
@Controller
public class HelloController {

    @Value("${sso.server.url}")
    String ssoServerUrl;


    //    无需登录
    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }


    @GetMapping("/employees")
    public String employees(Model model, HttpSession session,
                            @RequestParam(value = "token",required = false) String token){

        if(!StringUtils.isEmpty(token)){
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> forEntity = restTemplate.getForEntity("http://ssoserver.com:8080/userInfo?token="+token, String.class);
            String body = forEntity.getBody();
            session.setAttribute("loginUser",body);
        }

        Object loginUser = session.getAttribute("loginUser");
        if(loginUser==null){

            return "redirect:"+ssoServerUrl+"?redirect_url=http://client1.com:8081/employees";
        }else{
            List<String> emp= new ArrayList<>();
            emp.add("许可证");
            emp.add("平山县");
            model.addAttribute("emps",emp);
            return "list";
        }
    }
}

package com.mrxiao.gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mrxiao.common.utils.HttpUtils;
import com.mrxiao.common.utils.R;
import com.mrxiao.common.vo.MemberResponsVo;
import com.mrxiao.gulimall.auth.feign.MemberService;
import com.mrxiao.gulimall.auth.vo.SocialUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @ClassName OAuth2Controller
 * @Description TODO
 * @Version 1.0
 * @date 2021/1/9 0009 11:16
 */
@Controller
@Slf4j
public class OAuth2Controller {

    @Autowired
    MemberService memberService;

    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session) throws Exception {
        Map<String,String> map=new HashMap<>();
        map.put("client_id","3590122935");
        map.put("client_secret","7e338d15bfc047a2a19f83339dea5df3");
        map.put("grant_type","authorization_code");
        map.put("redirect_uri","http://auth.gulimall.com/oauth2.0/weibo/success");
        map.put("code",code);
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
//        https://api.weibo.com/oauth2/access_token?client_id=3590122935&client_secret=7e338d15bfc047a2a19f83339dea5df3&grant_type=authorization_code&redirect_uri=http://gulimall.com/success&code=ec9c4ac374e8a0e9ac8758d5647b096f
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", headers, map,bodys );
        if(response.getStatusLine().getStatusCode()==200){
            String json = EntityUtils.toString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);

            R oauthLogin = memberService.oauthLogin(socialUser);
            if(oauthLogin.getCode()==0){
                MemberResponsVo data = (MemberResponsVo) oauthLogin.getData("data", new TypeReference<MemberResponsVo>() {});
                log.info("登录成功,用户：{}",data.toString());
                session.setAttribute("loginUser",data);
                return "redirect:http://gulimall.com";
            }else {
                return "redirect:http://auth.gulimall.com/login.html";
            }
        }else{
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }

}


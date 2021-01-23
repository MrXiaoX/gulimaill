package com.mrxiao.gulimall.auth.feign;

import com.mrxiao.common.utils.R;
import com.mrxiao.gulimall.auth.vo.SocialUser;
import com.mrxiao.gulimall.auth.vo.UserLoginVo;
import com.mrxiao.gulimall.auth.vo.UserRegistVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Administrator
 * @ClassName MemberService
 * @Description TODO
 * @Version 1.0
 * @date 2020/12/22 0022 19:13
 */
@FeignClient("gulimall-member")
public interface MemberService {

    @PostMapping("member/member/regist")
    R regist(@RequestBody UserRegistVo userRegistVo);



    @PostMapping("member/member/login")
    R login(@RequestBody UserLoginVo memberLoginVo);


    @PostMapping("member/member/oauth2/login")
    R oauthLogin(@RequestBody SocialUser socialUser);
}

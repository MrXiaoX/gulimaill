package com.mrxiao.gulimall.auth.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Administrator
 * @ClassName SocialUser
 * @Description TODO
 * @Version 1.0
 * @date 2021/1/9 0009 12:43
 */
@Data
public class SocialUser implements Serializable {

    private String access_token;

    private String remind_in;

    private Long expires_in;

    private String uid;
}

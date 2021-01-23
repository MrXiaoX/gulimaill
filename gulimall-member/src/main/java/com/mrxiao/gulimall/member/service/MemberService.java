package com.mrxiao.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mrxiao.common.utils.PageUtils;
import com.mrxiao.gulimall.member.entity.MemberEntity;
import com.mrxiao.gulimall.member.exception.PhoneExistException;
import com.mrxiao.gulimall.member.exception.UserNameExistException;
import com.mrxiao.gulimall.member.vo.MemberLoginVo;
import com.mrxiao.gulimall.member.vo.MemberRegistVo;
import com.mrxiao.gulimall.member.vo.SocialUser;

import java.util.Map;

/**
 * 会员
 *
 * @author mrxiao
 * @email ismrxiao@163.com
 * @date 2020-05-13 19:31:55
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void regist(MemberRegistVo memberRegistVo);

    void checkPhoneUnique(String phone) throws PhoneExistException;


    void checkUserNameUnique(String userName) throws UserNameExistException;

    MemberEntity login(MemberLoginVo memberLoginVo);


    MemberEntity login(SocialUser socialUser);
}


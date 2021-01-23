package com.mrxiao.gulimall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrxiao.common.utils.HttpUtils;
import com.mrxiao.common.utils.PageUtils;
import com.mrxiao.common.utils.Query;
import com.mrxiao.gulimall.member.dao.MemberDao;
import com.mrxiao.gulimall.member.dao.MemberLevelDao;
import com.mrxiao.gulimall.member.entity.MemberEntity;
import com.mrxiao.gulimall.member.exception.PhoneExistException;
import com.mrxiao.gulimall.member.exception.UserNameExistException;
import com.mrxiao.gulimall.member.service.MemberService;
import com.mrxiao.gulimall.member.vo.MemberLoginVo;
import com.mrxiao.gulimall.member.vo.MemberRegistVo;
import com.mrxiao.gulimall.member.vo.SocialUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service("memberService")
@Slf4j
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(MemberRegistVo vo) {
        MemberDao memberDao = this.baseMapper;
        MemberEntity memberEntity = new MemberEntity();

        //设置默认等级
        MemberEntity levelId=memberLevelDao.getDefaultLevel();
        memberEntity.setLevelId(levelId.getId());

        //检测用户名和手机号是否唯一,使用异常机制，让controller感知异常
        checkPhoneUnique(vo.getPhone());
        checkUserNameUnique(vo.getUserName());

        memberEntity.setMobile(vo.getPhone());
        memberEntity.setUsername(vo.getUserName());
        memberEntity.setNickname(vo.getUserName());
        //密码进行加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(vo.getPassWord());
        memberEntity.setPassword(encode);
        memberDao.insert(memberEntity);
        log.info("====注册用户信息新增成功");
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException{
        MemberDao memberDao = this.baseMapper;
        Integer count = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("mobile",phone));
        if(count>0){
            throw new PhoneExistException();
        }

    }

    @Override
    public void checkUserNameUnique(String userName) throws UserNameExistException{
        MemberDao memberDao = this.baseMapper;
        Integer count = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("username",userName));
        if(count>0){
            throw new UserNameExistException();
        }
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        String loginacct = vo.getLoginacct();
        String passWord = vo.getPassWord();

        //1.去查询数据库
        MemberDao memberDao = this.baseMapper;
        MemberEntity memberEntity = memberDao.selectOne(new QueryWrapper<MemberEntity>().eq("username", loginacct)
                .or().eq("mobile", loginacct));
        if(memberEntity!=null){
            return null;
        }else{
            //2.获取数据库密码
            String passwordDb = memberEntity.getPassword();
            //进行页面与数据库匹配
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean encode = passwordEncoder.matches(passWord,passwordDb);
            if(encode){
                return memberEntity;
            }else{
                return null;
            }
        }
    }

    @Override
    public MemberEntity login(SocialUser socialUser) {
        //登录注册合并逻辑
        String uid = socialUser.getUid();
        //1.判断当前社交用户是否已经登录过系统
        MemberDao memberDao = this.baseMapper;
        MemberEntity memberEntity = memberDao.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
        if(memberEntity!=null){
            //表示已注册
            MemberEntity update = new MemberEntity();
            update.setId(memberEntity.getId());
            update.setAccessToken(socialUser.getAccess_token());
            update.setExpiresIn(socialUser.getExpires_in());

            memberDao.updateById(update);

            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            return memberEntity;
        }else{
            //2.没有查到当前社交用户对应的记录我们需要注册
            MemberEntity register = new MemberEntity();
            try {
            Map<String,String> query=new HashMap<>();
                query.put("access_token",socialUser.getAccess_token());
                query.put("uid",socialUser.getUid());
                HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<>(), query);
                if(response.getStatusLine().getStatusCode()==200){
                    String json = EntityUtils.toString(response.getEntity());
                    JSONObject jsonObject = JSON.parseObject(json);
                    String name = jsonObject.getString("name");
                    String gender = jsonObject.getString("gender");
                    register.setNickname(name);
                    register.setGender("m".equals(gender)?1:0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            register.setSocialUid(socialUser.getUid());
            register.setAccessToken(socialUser.getAccess_token());
            register.setExpiresIn(socialUser.getExpires_in());
            memberDao.insert(register);
            return register;
        }
    }

}
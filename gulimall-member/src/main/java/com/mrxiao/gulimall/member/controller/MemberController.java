package com.mrxiao.gulimall.member.controller;

import com.mrxiao.common.exception.BizCodeEnume;
import com.mrxiao.common.utils.PageUtils;
import com.mrxiao.common.utils.R;
import com.mrxiao.gulimall.member.entity.MemberEntity;
import com.mrxiao.gulimall.member.exception.PhoneExistException;
import com.mrxiao.gulimall.member.exception.UserNameExistException;
import com.mrxiao.gulimall.member.feign.CoupenFeignService;
import com.mrxiao.gulimall.member.service.MemberService;
import com.mrxiao.gulimall.member.vo.MemberLoginVo;
import com.mrxiao.gulimall.member.vo.MemberRegistVo;
import com.mrxiao.gulimall.member.vo.SocialUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;



/**
 * 会员
 *
 * @author mrxiao
 * @email ismrxiao@163.com
 * @date 2020-05-13 19:31:55
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    CoupenFeignService coupenFeignService;

    @PostMapping("/oauth2/login")
    public R oauthLogin(@RequestBody SocialUser socialUser){

        MemberEntity memberEntity= memberService.login(socialUser);
        if(memberEntity!=null){
            return R.ok().setData(memberEntity);
        }else{
            return R.error(BizCodeEnume.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getCode(),BizCodeEnume.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getMsg());
        }
    }

    @PostMapping("/coupons")
    public R test(){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("阿伟");

        R memerCoupon = coupenFeignService.memerCoupon();
        return R.ok().put("memer", memberEntity).put("coupons",memerCoupon.get("coupon"));
    }

    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo memberLoginVo){

        MemberEntity memberEntity= memberService.login(memberLoginVo);
        if(memberEntity!=null){
            return R.ok().setData(memberEntity);
        }else{
            return R.error(BizCodeEnume.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getCode(),BizCodeEnume.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getMsg());
        }
    }

    @PostMapping("/regist")
    public R regist(@RequestBody MemberRegistVo memberRegistVo){
        try {
            memberService.regist(memberRegistVo);
        } catch (PhoneExistException e) {
            return R.error(BizCodeEnume.PRODCUT_UP_EXCEPTION.getCode(),BizCodeEnume.PRODCUT_UP_EXCEPTION.getMsg());
        }catch (UserNameExistException e){
            return R.error(BizCodeEnume.USER_EXIST_EXCEPTION.getCode(),BizCodeEnume.USER_EXIST_EXCEPTION.getMsg());
        }
        return R.ok();
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member){
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member){
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}

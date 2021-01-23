package com.mrxiao.gulimall.member.dao;

import com.mrxiao.gulimall.member.entity.MemberEntity;
import com.mrxiao.gulimall.member.entity.MemberLevelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员等级
 * 
 * @author mrxiao
 * @email ismrxiao@163.com
 * @date 2020-05-13 19:31:55
 */
@Mapper
public interface MemberLevelDao extends BaseMapper<MemberLevelEntity> {

    MemberEntity getDefaultLevel();

}

package com.mrxiao.gulimall.member.dao;

import com.mrxiao.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author mrxiao
 * @email ismrxiao@163.com
 * @date 2020-05-13 19:31:55
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}

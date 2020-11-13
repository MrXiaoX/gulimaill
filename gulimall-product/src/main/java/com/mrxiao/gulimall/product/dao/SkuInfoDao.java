package com.mrxiao.gulimall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mrxiao.gulimall.product.entity.SkuInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * sku信息
 * 
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-01 21:08:49
 */
@Mapper
public interface SkuInfoDao extends BaseMapper<SkuInfoEntity> {
	
}

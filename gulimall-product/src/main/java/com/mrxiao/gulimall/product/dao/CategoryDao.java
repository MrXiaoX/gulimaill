package com.mrxiao.gulimall.product.dao;

import com.mrxiao.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author mrxiao
 * @email ismrxiao@163.com
 * @date 2020-05-12 19:47:03
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}

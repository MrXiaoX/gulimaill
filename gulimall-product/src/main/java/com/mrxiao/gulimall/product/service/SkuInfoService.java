package com.mrxiao.gulimall.product.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.mrxiao.common.utils.PageUtils;
import com.mrxiao.gulimall.product.entity.SkuInfoEntity;
import com.mrxiao.gulimall.product.vo.SkuItemVo;

import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-01 21:08:49
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);


    List<SkuInfoEntity> getSkuBySpuId(Long spuId);

    SkuItemVo item(Long skuId);

}


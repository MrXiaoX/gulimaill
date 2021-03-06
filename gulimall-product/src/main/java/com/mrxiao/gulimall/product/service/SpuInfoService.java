package com.mrxiao.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mrxiao.common.utils.PageUtils;
import com.mrxiao.gulimall.product.entity.SpuInfoEntity;
import com.mrxiao.gulimall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author mrxiao
 * @email ismrxiao@163.com
 * @date 2020-05-12 19:47:03
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo spuSaveVo);


    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

    /**
     * 商品上架
     * @param spuId
     */
    void up(Long spuId);

}

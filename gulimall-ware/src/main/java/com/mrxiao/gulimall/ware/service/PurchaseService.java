package com.mrxiao.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mrxiao.common.utils.PageUtils;
import com.mrxiao.gulimall.ware.entity.PurchaseEntity;
import com.mrxiao.gulimall.ware.vo.MergeVO;
import com.mrxiao.gulimall.ware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author mrxiao
 * @email ismrxiao@163.com
 * @date 2020-05-13 19:46:11
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceivePurchase(Map<String, Object> params);


    void mergePurchase(MergeVO mergeVO);

    void received(List<Long> ids);

    void done(PurchaseDoneVo purchaseDoneVo);

}


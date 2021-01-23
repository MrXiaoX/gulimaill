package com.mrxiao.gulimall.product.web;

import com.mrxiao.gulimall.product.service.SkuInfoService;
import com.mrxiao.gulimall.product.vo.SkuItemVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Administrator
 * @ClassName ItemController
 * @Description TODO
 * @Version 1.0
 * @date 2020/12/14 0014 19:30
 */
@Controller
@Slf4j
public class ItemController {

    @Autowired
    SkuInfoService service;

    @GetMapping({"/{skuId}.html"})
    public String skuItem(@PathVariable("skuId") Long skuId){
        log.info("准备查询"+skuId+"详情");
        SkuItemVo vo= service.item(skuId);

        return "item";
    }
}

package com.mrxiao.gulimall.gulimallseach.controller;


import com.mrxiao.common.exception.BizCodeEnume;
import com.mrxiao.common.to.es.SkuEsModel;
import com.mrxiao.common.utils.R;
import com.mrxiao.gulimall.gulimallseach.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequestMapping("/search/save")
@RestController
public class ElasticSearchController {

    @Autowired
    ProductSaveService productSaveService;

    //上架商品
    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels)  {
        boolean b=false;
        try {
            b = productSaveService.productStatusUp(skuEsModels);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("ElasticSearchController商品上架错误:{}",e);
            return R.error(BizCodeEnume.PRDOCUT_UP_EXCEPTION.getCode(),BizCodeEnume.PRDOCUT_UP_EXCEPTION.getMsg());
        }
        if(b){
            return  R.ok();
        }else{
            return R.error(BizCodeEnume.PRDOCUT_UP_EXCEPTION.getCode(),BizCodeEnume.PRDOCUT_UP_EXCEPTION.getMsg());
        }
    }
}

package com.mrxiao.gulimall.product.feign;

import com.mrxiao.common.to.es.SkuEsModel;
import com.mrxiao.common.utils.R;
import com.mrxiao.gulimall.product.feign.fallback.SearchFeignServiceFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(value = "gulimall-search",fallback = SearchFeignServiceFallBack.class )
public interface SearchFeignService{

    @PostMapping("/search/save/product")
    R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}

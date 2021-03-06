package com.mrxiao.gulimall.product.feign;

import com.mrxiao.common.to.SkuHasStockVo;
import com.mrxiao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gulimall-ware")
public interface WareFeignService {

    @PostMapping("/ware/waresku/hasstock")
    R<List<SkuHasStockVo>> getSkuHasStock(@RequestBody List<Long> skuIds);
}

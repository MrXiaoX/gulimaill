package com.mrxiao.gulimall.product.feign.fallback;

import com.mrxiao.common.exception.BizCodeEnume;
import com.mrxiao.common.to.es.SkuEsModel;
import com.mrxiao.common.utils.R;
import com.mrxiao.gulimall.product.feign.SearchFeignService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Administrator
 * @ClassName SearchFeignServiceFallBack
 * @Description TODO
 * @Version 1.0
 * @date 2021/1/28 0028 15:10
 */
@Component
public class SearchFeignServiceFallBack implements SearchFeignService {

    @Override
    public R productStatusUp(List<SkuEsModel> skuEsModels) {
        return R.error(BizCodeEnume.COLLETION_TOO_MUCH_EXCEPTION.getCode(),BizCodeEnume.COLLETION_TOO_MUCH_EXCEPTION.getMsg());
    }
}

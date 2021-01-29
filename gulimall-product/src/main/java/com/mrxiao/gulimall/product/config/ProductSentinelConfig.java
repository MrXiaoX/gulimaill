package com.mrxiao.gulimall.product.config;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.mrxiao.common.exception.BizCodeEnume;
import com.mrxiao.common.utils.R;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Administrator
 * @ClassName ProductSentineConfig
 * @Description TODO
 * @Version 1.0
 * @date 2021/1/27 0027 19:36
 */
@Component
public class ProductSentinelConfig  {
//    @Override  implements UrlBlockHandler
//    public void blocked(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws IOException {
//        // BlockException 异常接口，其子类为Sentinel五种规则异常的实现类
//        // AuthorityException 授权异常
//        // DegradeException 降级异常
//        // FlowException 限流异常
//        // ParamFlowException 参数限流异常
//        // SystemBlockException 系统负载异常
////        ResponseData data = new ResponseData();
////        if (e instanceof FlowException) {
////            data = new ResponseData(-1, "接口被限流了。");
////        } else if (e instanceof DegradeException) {
////            data = new ResponseData(-2, "接口被降级了。");
////        }
//        R error = R.error(BizCodeEnume.COLLETION_TOO_MUCH_EXCEPTION.getCode(), BizCodeEnume.COLLETION_TOO_MUCH_EXCEPTION.getMsg());
//        httpServletResponse.setCharacterEncoding("UTF8");
//        httpServletResponse.setContentType("application/json");
//        httpServletResponse.getWriter().write(JSON.toJSONString(error));
////        httpServletResponse.setContentType("application/json;charset=utf-8");
////        httpServletResponse.getWriter().write(JSON.toJSONString(data));
//    }


    public ProductSentinelConfig() {
        WebCallbackManager.setUrlBlockHandler(new UrlBlockHandler() {
            @Override
            public void blocked(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws IOException {
                R error = R.error(BizCodeEnume.COLLETION_TOO_MUCH_EXCEPTION.getCode(), BizCodeEnume.COLLETION_TOO_MUCH_EXCEPTION.getMsg());
                httpServletResponse.setCharacterEncoding("UTF8");
                httpServletResponse.setContentType("application/json");
                httpServletResponse.getWriter().write(JSON.toJSONString(error));
            }
        });
    }
}

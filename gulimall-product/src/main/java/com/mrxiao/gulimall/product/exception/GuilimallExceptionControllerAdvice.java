package com.mrxiao.gulimall.product.exception;

import com.mrxiao.common.exception.BizCodeEnume;
import com.mrxiao.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages={"com.mrxiao.gulimall.product.controller"})
public class GuilimallExceptionControllerAdvice
{

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handlerVaildException(MethodArgumentNotValidException e){
        log.info("数据校验出现问题{},异常类型{}",e.getMessage(),e.getClass());
        BindingResult result=e.getBindingResult();
        Map<String,Object> map=new HashMap<>();
        result.getFieldErrors().forEach((item)-> map.put(item.getField(),item.getDefaultMessage()));
        return R.error(BizCodeEnume.VAILD_EXCEPTION.getCode(),BizCodeEnume.VAILD_EXCEPTION.getMsg()).put("data",map);
    }

    @ExceptionHandler(value = Throwable.class)
    public R handlerException(Throwable throwable){
        log.error("错误：",throwable);
        return R.error(BizCodeEnume.VAILD_EXCEPTION.getCode(),BizCodeEnume.VAILD_EXCEPTION.getMsg());
    }

}

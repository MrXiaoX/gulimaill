package com.mrxiao.gulimall.member.exception;

/**
 * @author Administrator
 * @ClassName PhoneExistException
 * @Description TODO
 * @Version 1.0
 * @date 2020/12/17 0017 19:52
 */
public class PhoneExistException extends RuntimeException {
    public PhoneExistException() {
        super("手机号已存在");
    }
}

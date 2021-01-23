package com.mrxiao.gulimall.member.exception;

/**
 * @author Administrator
 * @ClassName PhoneExistException
 * @Description TODO
 * @Version 1.0
 * @date 2020/12/17 0017 19:52
 */
public class UserNameExistException extends RuntimeException {
    public UserNameExistException() {
        super("用户名已存在");
    }
}

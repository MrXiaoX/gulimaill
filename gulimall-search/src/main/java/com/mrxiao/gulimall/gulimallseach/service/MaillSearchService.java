package com.mrxiao.gulimall.gulimallseach.service;

import com.mrxiao.gulimall.gulimallseach.vo.SearchParam;
import com.mrxiao.gulimall.gulimallseach.vo.SearchResult;

/**
 * @author Administrator
 * @ClassName SearchController
 * @Description TODO
 * @Version 1.0
 * @date 2020/11/17 0017 20:00
 */
public interface MaillSearchService {

    /**
     * @Author mrxiao
     * @Description //检索所以参数
     * @Date 20:22 2020/11/17 0017
     * @Version 1.0
     * @Param com.mrxiao.gulimall.gulimallseach.vo.SearchResult
     * @return com.mrxiao.gulimall.gulimallseach.vo.SearchResult 返回检索的结果，里面包含页面所需所有信息
     **/
    SearchResult search(SearchParam searchParam);

}

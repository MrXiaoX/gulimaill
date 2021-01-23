package com.mrxiao.gulimall.gulimallseach.controller;

import com.mrxiao.gulimall.gulimallseach.service.MaillSearchService;
import com.mrxiao.gulimall.gulimallseach.vo.SearchParam;
import com.mrxiao.gulimall.gulimallseach.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Administrator
 * @ClassName SearchController
 * @Description 搜索
 * @Version 1.0
 * @date 2020/11/16 0016 19:59
 */
@Controller
public class SearchController {

    @Autowired
    MaillSearchService maillSearchService;


    /**
     * @Author mrxiao
     * @Description //自动将页面提交过来的请求参数封装成指定对象
     * @Date 20:03 2020/11/17 0017
     * @Version 1.0
     * @Param java.lang.String
     * @return java.lang.String
     **/
    @GetMapping("/list.html")
    public String listPage(SearchParam searchParam, Model model){
        //1.根据传递来的页面查询参数，去es中检索商品
        SearchResult result= maillSearchService.search(searchParam);
        model.addAttribute("result",result);
        System.out.println("搜索");
        return "list";
    }
}

package com.xueliandan.gulimall.gulisearch.controller;

import com.xueliandan.gulimall.gulisearch.service.IMallSearchService;
import com.xueliandan.gulimall.gulisearch.vo.SearchParam;
import com.xueliandan.gulimall.gulisearch.vo.SearchResp;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;

/**
 * @author zxb 2026/2/2 20:48
 */
@Controller
public class SearchController {

    @Resource
    private IMallSearchService mallSearchService;


    /**
     * 用户单击商品分类，访问 list.html 请求，跳转到 list.html 界面
     * 这里将页面提交的所有请求参数封装到 SearchParam 里面
     * 然后调用 service 层，将数据返回给前端
     *
     * @return list.html 界面
     */
    @GetMapping(path = "/list.html")
    public String listPage(SearchParam searchParam, Model model) {
        // 根据参数查询 ES 获取数据
        SearchResp retVal = mallSearchService.search(searchParam);
        model.addAttribute("result", retVal);
        return "list";
    }


}

package com.xueliandan.gulimall.product.web;

import com.xueliandan.gulimall.product.entity.PmsCategoryEntity;
import com.xueliandan.gulimall.product.entity.vo.Catelog2Vo;
import com.xueliandan.gulimall.product.service.PmsCategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author zxb 2025/12/9 21:37
 */
@Controller
@RequestMapping(path = "/product")
public class IndexController {


    @Resource
    private PmsCategoryService categoryService;


    @GetMapping(path = {"/index.html", "/"})
    public String index(Model model) {

        // 查询出一级分类放到请求域中，模板引擎中就能拿到我们塞到请求域中的一级分类数据
        List<PmsCategoryEntity> categoryEntities = categoryService.findFirstLevelCategory();
        model.addAttribute("categories", categoryEntities);
        // thymeleaf 的默认前缀: classpath:/templates/
        // 默认后缀:  .hml
        // 因此这里直接返回 html 文件名称即可跳转，前后缀视图解析器会自动拼接
        return "index";
    }

    @GetMapping(value = "/index/catalog.json")
    @ResponseBody
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        return categoryService.getCatalogJson();
    }

    @GetMapping(path = "/hi")
    @ResponseBody
    public String hi() {
        return "hi";
    }

}

package com.my.blog.controller;

import com.my.blog.common.Result;
import com.my.blog.dto.request.ArticleCategoryCreateDTO;
import com.my.blog.dto.request.ArticleCategoryDeleteDTO;
import com.my.blog.entity.Article;
import com.my.blog.entity.ArticleCategory;
import com.my.blog.service.ArticleCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "文章分类管理", description = "category create/delete/query") // Swagger 文档标签
@RestController
@RequestMapping("/api/categories")
public class ArticleCategoryController {

    @Autowired
    private ArticleCategoryService articleCategoryService;

    //创建分类
    @PostMapping("/create")  //测试通过
    public Result create(@RequestBody @Validated ArticleCategoryCreateDTO DTO){
        ArticleCategory articleCategory = articleCategoryService.createCategory(DTO);
        return Result.success("articleCategory_create_success",articleCategory);
    }

    //查询所有分类
    @GetMapping("/queryAll")  //测试通过
    public Result queryAll(){
        List<ArticleCategory> articleCategories = articleCategoryService.selectAll();

        return Result.success("query_allCategory_success",articleCategories);
    }

    @PreAuthorize("hasRole('ADMIN')")  //测试通过
    @PostMapping("/delete")  //根据名字删除分类
    public Result deleteByName(@RequestBody ArticleCategoryDeleteDTO dto){
        int i = articleCategoryService.deleteByName(dto.getCategoryName());
        return Result.success("category_delete_success",i);
    }

    @Operation(summary = "getArticlesByCategoryName", description = "根据分类名称获取所有文章")  //测试通过
    @GetMapping("/name/{categoryName}/articles")  //根据分类名称获取所有文章
    public Result getArticlesByCategoryName(@PathVariable String categoryName) {
        List<Article> articles = articleCategoryService.getArticlesByCategoryName(categoryName);
        return Result.success("get_category_articles_success", articles);
    }

}

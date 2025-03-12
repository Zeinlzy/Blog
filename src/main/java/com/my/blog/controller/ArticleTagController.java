package com.my.blog.controller;

import com.my.blog.common.Result;
import com.my.blog.dto.request.ArticleTagCreateDTO;
import com.my.blog.dto.request.ArticleTagDeleteDTO;
import com.my.blog.entity.Article;
import com.my.blog.entity.ArticleCategory;
import com.my.blog.entity.ArticleTag;
import com.my.blog.service.Impl.ArticleTagServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Tag management", description = "Tag create/delete/query") // Swagger 文档标签
@RestController
@RequestMapping("/api/tags")
public class ArticleTagController {

    @Autowired
    private ArticleTagServiceImpl articleTagServiceImpl;

    @PreAuthorize("hasRole('ADMIN')")  //测试通过
    @PostMapping("/create")  //创建标签
    public Result create(@RequestBody @Validated ArticleTagCreateDTO articleTagCreateDTO) {
        ArticleTag tag = articleTagServiceImpl.createTag(articleTagCreateDTO);
        return Result.success("tag_create_success", tag);
    }

    @PreAuthorize("hasRole('ADMIN')") //测试通过
    @PostMapping("/delete")  //根据标签名删除标签
    public Result delete(@RequestBody @Validated ArticleTagDeleteDTO articleTagDeleteDTO) {
        int result = articleTagServiceImpl.deleteByName(articleTagDeleteDTO.getTagName());
        return Result.success("tag_delete_success", result);
    }

    //查询所有标签
    @GetMapping("/queryAll")  //测试通过
    public Result queryAll(){
        List<ArticleTag> Tags = articleTagServiceImpl.selectAll();

        return Result.success("query_allTag_success",Tags);
    }

    @Operation(summary = "getTagsByArticleId", description = "获取文章的所有标签")  //测试通过
    @GetMapping("/article/{articleId}")  //获取某篇文章的所有标签
    public Result getTagsByArticleId(@PathVariable Long articleId) {
        List<ArticleTag> tags = articleTagServiceImpl.getTagsByArticleId(articleId);
        return Result.success("get_article_tags_success", tags);
    }

    @Operation(summary = "getArticlesByTagId", description = "获取使用某个标签的所有文章")  //测试通过
    @GetMapping("/{tagId}/articles")  //获取使用某个标签的所有文章
    public Result getArticlesByTagId(@PathVariable Long tagId) {
        List<Article> articles = articleTagServiceImpl.getArticlesByTagId(tagId);
        return Result.success("get_tag_articles_success", articles);
    }
}
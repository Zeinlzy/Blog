package com.my.blog.controller;

import com.my.blog.common.Result;
import com.my.blog.dto.request.ArticleTagCreateDTO;
import com.my.blog.dto.request.ArticleTagDeleteDTO;
import com.my.blog.entity.ArticleCategory;
import com.my.blog.entity.ArticleTag;
import com.my.blog.service.Impl.ArticleTagServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Tag management", description = "Tag create/delete/query") // Swagger 文档标签
@RestController
@RequestMapping("/api/tags")
public class ArticleTagController {

    @Autowired
    private ArticleTagServiceImpl articleTagServiceImpl;

    //http://localhost:8080/api/tags/create
    @PostMapping("/create")  //创建标签
    public Result create(@RequestBody @Validated ArticleTagCreateDTO articleTagCreateDTO) {
        ArticleTag tag = articleTagServiceImpl.createTag(articleTagCreateDTO);
        return Result.success("tag_create_success", tag);
    }

    //http://localhost:8080/api/tags/delete
    @PostMapping("/delete")  //根据标签名删除标签
    public Result delete(@RequestBody @Validated ArticleTagDeleteDTO articleTagDeleteDTO) {
        int result = articleTagServiceImpl.deleteByName(articleTagDeleteDTO.getTagName());
        return Result.success("tag_delete_success", result);
    }

    //http://localhost:8080/api/tags/queryAll
    @GetMapping("/queryAll")  //查询所有分类
    public Result queryAll(){
        List<ArticleTag> Tags = articleTagServiceImpl.selectAll();

        return Result.success("query_allTag_success",Tags);
    }
}
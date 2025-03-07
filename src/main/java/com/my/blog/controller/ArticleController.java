package com.my.blog.controller;

import com.my.blog.common.Result;
import com.my.blog.dto.request.ArticleCreateDTO;
import com.my.blog.entity.Article;
import com.my.blog.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

@Tag(name = "article management", description = "article create/delete/query") // Swagger 文档标签
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    //http://localhost:8080/api/articles/create
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "create", description = "create a article")
    @PostMapping("/create")  //控制层进行参数校验，服务层进行判断参数是否已经存在
    public Result create(@RequestBody @Validated ArticleCreateDTO createDTO) {  //如果Controller层没有添加@Valid注解，所有DTO校验规则都会被绕过
        Article article = articleService.createArticle(createDTO);
        return Result.success("article_create_success", article);
    }




    //DELETE 请求通常不携带请求体（部分浏览器或框架不支持）。
    //删除操作应通过路径参数 或 查询参数 定位资源。
    //http://localhost:8080/api/articles/{articleId}
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "deleteArticleByArticleId", description = "delete a article")
    @DeleteMapping("/{articleId}")  //控制层进行参数校验，服务层进行判断参数是否已经存在
    public Result deleteArticleByArticleId(@PathVariable Long articleId) {  //如果Controller层没有添加@Valid注解，所有DTO校验规则都会被绕过
        int result = articleService.deleteArticleByArticleId(articleId);

        if (result == 0){
            return Result.success("没有可删除的文章",null);
        }
        return Result.success("article_delete_success", result);
    }




    //
    // /api/articles/authors/{authorId}
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "deleteArticleByAuthorId", description = "delete articles")
    @DeleteMapping("/authors/{authorId}")  //控制层进行参数校验，服务层进行判断参数是否已经存在
    public Result deleteArticleByAuthorId(@PathVariable Long authorId) {  //如果Controller层没有添加@Valid注解，所有DTO校验规则都会被绕过

        int result = articleService.deleteArticleByAuthorId(authorId);

        if (result == 0){
            return Result.success("该作者没有可删除的文章",null);
        }
        return Result.success("article_delete_success", result);
    }


    //http://localhost:8080/api/articles/title
    @Operation(summary = "queryArticleByTitle", description = "query article")
    @PostMapping("/title")
    public Result selectArticleByTitle(String title){
        List<Article> article = articleService.selectArticleByTitle(title);
        return Result.success("query_article_success",article);
    }

}

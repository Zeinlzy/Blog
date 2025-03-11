package com.my.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
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

    @Operation(summary = "创建文章", description = "create a article")  //测试通过
    @PostMapping("/create")  //控制层进行参数校验，服务层进行判断参数是否已经存在
    public Result create(@RequestBody @Validated ArticleCreateDTO createDTO) {  //如果Controller层没有添加@Valid注解，所有DTO校验规则都会被绕过
        Article article = articleService.createArticle(createDTO);
        return Result.success("article_create_success", article);
    }

    //DELETE 请求通常不携带请求体（部分浏览器或框架不支持）。
    //删除操作应通过路径参数 或 查询参数 定位资源。
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "根据文章id删除文章", description = "delete a article")  //测试通过
    @DeleteMapping("/{articleId}")  //控制层进行参数校验，服务层进行判断参数是否已经存在
    public Result deleteArticleByArticleId(@PathVariable Long articleId) {  //如果Controller层没有添加@Valid注解，所有DTO校验规则都会被绕过
        int result = articleService.deleteArticleByArticleId(articleId);

        if (result == 0){
            return Result.success("没有可删除的文章",null);
        }
        return Result.success("article_delete_success", result);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "根据作者id删除文章", description = "delete articles")  //测试通过
    @DeleteMapping("/authors/{authorId}")  //控制层进行参数校验，服务层进行判断参数是否已经存在
    public Result deleteArticleByAuthorId(@PathVariable Long authorId) {  //如果Controller层没有添加@Valid注解，所有DTO校验规则都会被绕过

        int result = articleService.deleteArticleByAuthorId(authorId);

        if (result == 0){
            return Result.success("该作者没有可删除的文章",null);
        }
        return Result.success("article_delete_success", result);
    }


    @Operation(summary = "queryArticleByTitle", description = "query article")  //测试通过
    @PostMapping("/title")
    public Result selectArticleByTitle(String title){
        List<Article> article = articleService.selectArticleByTitle(title);
        return Result.success("query_article_success",article);
    }

    @Operation(summary = "获取所有文章", description = "分页获取所有文章信息")  //测试通过
    @GetMapping("/queryAllArticles")
    public Result<IPage<Article>> getAllArticles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<Article> articlePage = articleService.getAllArticles(page, size);
        return Result.success("获取文章列表成功", articlePage);
    }

    @Operation(summary = "获取文章详情", description = "根据文章ID获取文章详情")  //测试通过
    @GetMapping("/{articleId}")
    public Result getArticleById(@PathVariable Long articleId) {

        Article article = articleService.getArticleById(articleId);

        if (article == null) {
            return Result.error(400, "文章不存在");
        }
        return Result.success("获取文章详情成功", article);

    }

}

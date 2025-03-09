package com.my.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.my.blog.common.Result;
import com.my.blog.entity.User;
import com.my.blog.entity.Article;
import com.my.blog.dto.request.ArticleUpdateDTO;
import com.my.blog.service.UserService;
import com.my.blog.service.ArticleService;
import com.my.blog.service.AdminArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理员接口", description = "提供管理员相关功能")
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // 确保只有ADMIN角色可以访问
public class AdminController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private AdminArticleService adminArticleService;

    @Operation(summary = "获取所有用户", description = "分页获取所有用户信息")
    @GetMapping("/users")
    public Result<IPage<User>> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<User> userPage = userService.getAllUsers(page, size);
        return Result.success("获取用户列表成功", userPage);
    }
    
    @Operation(summary = "修改用户状态", description = "启用或禁用用户账号")
    @PutMapping("/users/{username}/status")
    public Result updateUserStatus(
            @PathVariable String username,
            @RequestParam boolean enabled) {
        userService.updateUserStatus(username, enabled);
        return Result.success("用户状态更新成功", null);
    }

    @Operation(summary = "修改用户角色", description = "更改用户的角色权限")
    @PutMapping("/users/{username}/role")
    public Result updateUserRole(
            @PathVariable String username,
            @RequestParam String role) {
        userService.updateUserRole(username, role);
        return Result.success("用户角色更新成功", null);
    }

    //****************************************************************
    // 文章管理相关接口
    
    @Operation(summary = "获取所有文章", description = "分页获取所有文章信息")  //测试通过
    @GetMapping("/articles")
    public Result<IPage<Article>> getAllArticles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<Article> articlePage = adminArticleService.getAllArticles(page, size);
        return Result.success("获取文章列表成功", articlePage);
    }
    
    @Operation(summary = "修改文章内容", description = "管理员修改不合适的文章内容") //测试通过
    @PutMapping("/articles/{articleId}")
    public Result updateArticle(
            @PathVariable Long articleId,
            @RequestBody ArticleUpdateDTO articleUpdateDTO) {
        Article article = adminArticleService.updateArticle(articleId, articleUpdateDTO);
        return Result.success("文章更新成功", article);
    }
    
    @Operation(summary = "删除文章", description = "管理员删除不合适的文章")   //通过测试
    @DeleteMapping("/articles/{articleId}")
    public Result deleteArticle(@PathVariable Long articleId) {
        adminArticleService.deleteArticle(articleId);
        return Result.success("文章删除成功", null);
    }
    
    @Operation(summary = "审核文章", description = "管理员审核文章内容") //测试通过
    @PutMapping("/articles/{articleId}/review")
    public Result reviewArticle(
            @PathVariable Long articleId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String reason) {
        adminArticleService.reviewArticle(articleId, approved, reason);
        return Result.success("文章审核完成", null);
    }

    //****************************************************************
}
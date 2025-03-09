package com.my.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.my.blog.dto.request.ArticleUpdateDTO;
import com.my.blog.entity.Article;

public interface AdminArticleService {

    /**
     * 获取所有文章（分页）
     * @param page 页码
     * @param size 每页大小
     * @return 文章分页结果
     */
    IPage<Article> getAllArticles(int page, int size);

    /**
     * 更新文章内容
     * @param articleId 文章ID
     * @param articleUpdateDTO 文章更新DTO
     * @return 更新后的文章
     */
    Article updateArticle(Long articleId, ArticleUpdateDTO articleUpdateDTO);

    /**
     * 删除文章
     * @param articleId 文章ID
     */
    void deleteArticle(Long articleId);

    /**
     * 审核文章
     * @param articleId 文章ID
     * @param approved 是否通过
     * @param reason 拒绝原因（如果不通过）
     */
    void reviewArticle(Long articleId, boolean approved, String reason);
}
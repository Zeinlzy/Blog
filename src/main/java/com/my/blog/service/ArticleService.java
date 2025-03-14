package com.my.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.my.blog.dto.request.ArticleCreateDTO;
import com.my.blog.entity.Article;

import java.util.List;

public interface ArticleService {

    Article createArticle(ArticleCreateDTO articleDTO);


    int deleteArticleByArticleId(Long articleId);

    int deleteArticleByAuthorId(Long authorId);

    List selectArticleByTitle(String title);

    IPage<Article> getAllArticles(int page, int size);

    /**
     * 根据ID查询文章
     * @param articleId 文章ID
     * @return 文章详情
     */
    Article getArticleById(Long articleId);

    // 添加增加浏览量的方法
    void incrementViewCount(Long articleId);



}

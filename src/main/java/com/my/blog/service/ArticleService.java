package com.my.blog.service;

import com.my.blog.dto.request.ArticleCreateDTO;
import com.my.blog.entity.Article;

import java.util.List;

public interface ArticleService {

    Article createArticle(ArticleCreateDTO articleDTO);


    int deleteArticleByArticleId(Long articleId);

    int deleteArticleByAuthorId(Long authorId);

    List selectArticleByTitle(String title);



}

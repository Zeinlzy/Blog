package com.my.blog.service;

import com.my.blog.dto.request.ArticleTagCreateDTO;
import com.my.blog.entity.Article;
import com.my.blog.entity.ArticleCategory;
import com.my.blog.entity.ArticleTag;

import java.util.List;

public interface ArticleTagService {
    boolean findByName();

    ArticleTag createTag(ArticleTagCreateDTO articleTagDTO);

    List selectAll();

    int deleteByName(String tagName);

    // 根据文章ID获取所有标签
    List<ArticleTag> getTagsByArticleId(Long articleId);

    List<Article> getArticlesByTagId(Long tagId);

}

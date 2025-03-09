package com.my.blog.service;

import com.my.blog.dto.request.ArticleCategoryCreateDTO;
import com.my.blog.entity.Article;
import com.my.blog.entity.ArticleCategory;

import java.util.List;

public interface ArticleCategoryService {

    ArticleCategory createCategory(ArticleCategoryCreateDTO articleCategoryCreateDTO);

    List selectAll();

    int deleteByName(String categoryName);


    // 根据分类名称获取该分类下的所有文章
    List<Article> getArticlesByCategoryName(String categoryName);
}

package com.my.blog.service;

import com.my.blog.dto.request.ArticleCategoryCreateDTO;
import com.my.blog.entity.ArticleCategory;

import java.util.List;

public interface ArticleCategoryService {

    ArticleCategory createCategory(ArticleCategoryCreateDTO articleCategoryCreateDTO);

    List selectAll();

    int deleteByName(String categoryName);
}

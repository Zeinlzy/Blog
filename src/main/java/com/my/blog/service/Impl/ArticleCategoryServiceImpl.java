package com.my.blog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.my.blog.dto.request.ArticleCategoryCreateDTO;
import com.my.blog.entity.ArticleCategory;
import com.my.blog.exception.CustomException;
import com.my.blog.exception.ErrorCode;
import com.my.blog.repository.ArticleCategoryRepository;
import com.my.blog.service.ArticleCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ArticleCategoryServiceImpl implements ArticleCategoryService {

    private final ArticleCategoryRepository articleCategoryRepository;

    @Override
    public ArticleCategory createCategory(ArticleCategoryCreateDTO articleCategoryCreateDTO) {

        //分类已存在
        if (articleCategoryRepository.existsByName(articleCategoryCreateDTO.getCategoryName())){
            throw new CustomException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        //创建分类
        ArticleCategory articleCategory = ArticleCategory.builder()
                .categoryName(articleCategoryCreateDTO.getCategoryName())
                .description(articleCategoryCreateDTO.getDescription())
                .build();

        //插入分类
        articleCategoryRepository.insert(articleCategory);

        return articleCategory;

    }

    @Override
    public List<ArticleCategory> selectAll() {
        return articleCategoryRepository.selectList(new QueryWrapper<>());

    }

    @Override
    public int deleteByName(String categoryName) {

        //如果没查询到分类，返回错误信息
        int result = articleCategoryRepository.deleteByName(categoryName);
        if (result == 0){
            throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        return result;



    }
}

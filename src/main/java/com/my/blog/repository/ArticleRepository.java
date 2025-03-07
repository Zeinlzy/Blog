package com.my.blog.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.my.blog.entity.Article;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ArticleRepository extends BaseMapper<Article> {

    int deleteArticleByAuthorId(Long authorId);

    List<Long> selectArticleIdsByAuthorId(Long authorId);

    int deleteByArticleIds(Long articleId);

    List<Article> selectArticleByTitle(String Title);
}

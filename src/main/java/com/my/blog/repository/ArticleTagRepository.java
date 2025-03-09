package com.my.blog.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.my.blog.entity.Article;
import com.my.blog.entity.ArticleTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ArticleTagRepository extends BaseMapper<ArticleTag> {
    
    // 根据标签名查找标签是否存在
    boolean findByName(String tagName);
    
    // 在现有的 ArticleTagRepository 接口中添加以下方法
    /**
     * 根据标签名称查询标签
     * @param tagName 标签名称
     * @return 标签对象
     */
    ArticleTag selectByName(String tagName);
    
    // 根据标签ID查找标签
    ArticleTag findById(Long tagId);
    
    // 根据标签ID查找使用该标签的所有文章
    List<Article> findArticlesByTagId(Long tagId);
    
    // 根据文章ID查找该文章的所有标签
    List<ArticleTag> findTagsByArticleId(Long articleId);
    
    // 根据标签名删除标签
    int deleteByName(String tagName);
}

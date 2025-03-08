package com.my.blog.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.my.blog.entity.Article;
import com.my.blog.entity.ArticleCategory;
import com.my.blog.entity.ArticleTag;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleTagRepository extends BaseMapper<ArticleTag> {
    @Select("SELECT * FROM article_tag WHERE tag_id = #{tagId}")
    ArticleTag findById(Long tagId);  //根据标签id查询标签

    @Select("SELECT COUNT(*)>0 FROM article_tag WHERE tag_name = #{tagName}")
    Boolean findByName(String tagName); //根据标签名判断标签是否存在

    //查询所有标签


    //根据标签名删除标签
    @Delete("DELETE from article_tag WHERE tag_name = #{tagName}")
    int deleteByName(String tagName);

    //此处有插入标签方法

    //根据文章ID查询所有标签
    List<ArticleTag> findTagsByArticleId(Long articleId);

    //根据标签ID查询所有使用该标签的文章
    List<Article> findArticlesByTagId(Long tagId);
}

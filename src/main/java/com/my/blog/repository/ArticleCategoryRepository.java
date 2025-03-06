package com.my.blog.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.my.blog.entity.ArticleCategory;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ArticleCategoryRepository extends BaseMapper<ArticleCategory> {

    @Select("SELECT * FROM article_category WHERE category_name = #{categoryName}")
    ArticleCategory findByName(String categoryName);

    @Select("SELECT COUNT(*)>0 FROM article_category WHERE category_name = #{categoryName}")
    boolean existsByName(String categoryName);

    //此处有查询所有属性的方法


    //根据分类名删除分类属性,返回类型只能是int
    @Delete("DELETE from article_category WHERE category_name = #{categoryName}")
    int deleteByName(String categoryName);


}

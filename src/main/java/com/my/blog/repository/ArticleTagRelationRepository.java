package com.my.blog.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.my.blog.entity.ArticleTagRelation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ArticleTagRelationRepository extends BaseMapper<ArticleTagRelation> {

    void deleteByArticleIds(List articleIds);
}

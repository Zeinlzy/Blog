package com.my.blog.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.my.blog.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ArticleRepository extends BaseMapper<Article> {
    
    List<Long> selectArticleIdsByAuthorId(Long authorId);
    
    void deleteByArticleIds(@Param("articleIds") List<Long> articleIds);
    
    int deleteArticleByAuthorId(Long authorId);
    
    List<Article> selectArticleByTitle(String title);
    
    // 新增的方法
    // 在现有的 ArticleRepository 接口中添加以下方法
    /**
     * 删除文章标签关联
     * @param articleId 文章ID
     */
    void deleteArticleTagRelations(Long articleId);
    
    /**
     * 插入文章标签关联
     * @param articleId 文章ID
     * @param tagId 标签ID
     */
    void insertArticleTagRelation(@Param("articleId") Long articleId, @Param("tagId") Long tagId);

    // 在现有的ArticleRepository接口中添加以下方法
    @Select("SELECT * FROM article ORDER BY view_count DESC LIMIT #{limit}")
    List<Article> findTopNByViewCount(int limit);

    // 添加更新浏览量的方法
    @Update("UPDATE article SET view_count = view_count + 1 WHERE article_id = #{articleId}")
    int incrementViewCount(Long articleId);

}

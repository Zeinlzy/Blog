package com.my.blog.entity;

import jakarta.persistence.*;
import lombok.Data;

// ArticleTagRelation.java，实现文章和标签的多对多关系
@Entity
@Table(name = "article_tag_relation")
@Data
@IdClass(ArticleTagRelationId.class)  // 指定复合主键类
public class ArticleTagRelation {
    @Id
    @Column(name = "article_id")
    private Long articleId;

    @Id
    @Column(name = "tag_id")
    private Long tagId;

    // 关联字段
    @ManyToOne
    @JoinColumn(name = "article_id", insertable = false, updatable = false)
    private Article article;

    @ManyToOne
    @JoinColumn(name = "tag_id", insertable = false, updatable = false)
    private ArticleTag tag;
}



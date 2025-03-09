package com.my.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 *
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "article")
@Data
public class Article {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long articleId;  //文章id,主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // JPA 自增
    @TableId(type = IdType.AUTO) // MyBatis-Plus 自增
    private Long articleId;  // 主键
    
    @Column(nullable = false, length = 200)
    private String title;  //文章标题
    
    @Column(name = "author_id", nullable = false, updatable = false)
    private Long authorId;  //作者id
    
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP) // 明确时间类型
    private LocalDateTime publishTime;  //发布时间
    
    @Lob  // 大文本字段
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; //文章内容

    //name = "category_id指定当前表中外键列的名称,referencedColumnName = "categoryId"指明外键关联的目标列（即分类表中的主键列名 categoryId）
    // 修改这部分代码
    @ManyToOne  //表示当前实体（如 Article 文章）的多个实例可以关联到另一个实体
    @JoinColumn(name = "category_id", referencedColumnName = "category_id") // 修改referencedColumnName为"category_id"
    private ArticleCategory category;  //文章分类
}
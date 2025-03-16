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
    @TableId(value = "article_id", type = IdType.AUTO) // MyBatis-Plus 自增
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

    @Column(name = "category") // 直接映射到数据库的字符串字段
    private String category;  // 存储分类名称（如 "技术"）

    // 在现有的 Article 类中添加以下字段和方法
    private String summary;

    private LocalDateTime updateTime;

    private String status = "draft"; // 文章状态：draft, approved, rejected

    private String rejectReason; // 拒绝原因

    // 添加浏览量字段
    @Column(name = "view_count")
    private Integer viewCount = 0; // 默认值为0

}
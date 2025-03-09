package com.my.blog.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

// ArticleCategory.java
@Builder
@Entity // 添加JPA注解
@Table(name = "article_category") // 添加JPA注解
@TableName("article_category")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCategory {

    @Id // 添加JPA注解
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 添加JPA注解
    @TableId(value = "category_id",type = IdType.AUTO)  // MyBatis-Plus 自增主键
    private Long categoryId;

    @Column(nullable = false, unique = true, length = 50) // 添加JPA注解
    @TableField(value = "category_name")
    private String categoryName;

    @Column(length = 500) // 添加JPA注解
    @TableField(value = "description")
    private String description;

    /**
     * 父分类ID：parent_category_id 数据库列映射到 parentCategoryIdValue 字段，​存储实际 ID 值。
     */
    @Column(name = "parent_category_id") // 添加JPA注解
    @TableField(value = "parent_category_id")
    private Long parentCategoryIdValue; // 存储父分类ID的值

    /**
     * 父分类对象 - 通过手动查询设置，不由MyBatis-Plus直接管理
     * parentCategoryId 和 children 标记为 exist = false，​避免 MyBatis-Plus 尝试映射到数据库字段。
     */
    @ManyToOne(fetch = FetchType.LAZY) // 添加JPA注解
    @JoinColumn(name = "parent_category_id", insertable = false, updatable = false) // 添加JPA注解，但设置为不可插入和更新
    @TableField(exist = false)
    private ArticleCategory parentCategoryId;

    /**
     * 子分类列表 - 通过手动查询设置，不由MyBatis-Plus直接管理
     */
    @OneToMany(mappedBy = "parentCategoryId", fetch = FetchType.LAZY) // 添加JPA注解
    @TableField(exist = false)
    @ToString.Exclude // 避免循环toString
    @EqualsAndHashCode.Exclude // 避免循环equals
    private List<ArticleCategory> children = new ArrayList<>();
}
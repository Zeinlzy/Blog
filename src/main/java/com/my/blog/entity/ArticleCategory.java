package com.my.blog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

// ArticleCategory.java
@Builder
@Entity
@Table(name = "article_category")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;
    
    @Column(nullable = false, unique = true, length = 50)
    private String categoryName;
    
    @Column(length = 500)
    private String description;

    /**
     * name = "parent_category_id:在当前实体对应的数据库表**中，外键列的列名为 parent_category_id。例如，分类表（article_category）会有一个 parent_category_id 列，存储父分类的主键值。
     * referencedColumnName 的省略**：
     * 默认指向目标实体（ArticleCategory）的主键列。若目标主键列名为 category_id，则等价于 referencedColumnName = "category_id"。如果目标主键名与默认值不同，需显式指定
     */
    @ManyToOne(fetch = FetchType.LAZY) //fetch = FetchType.LAZY 作用：启用延迟加载策略。
    @JoinColumn(name = "parent_category_id")
    private ArticleCategory parentCategoryId;
    
    @OneToMany(mappedBy = "parentCategory", fetch = FetchType.LAZY) //mappedBy = "parentCategory":外键的实际管理权在子分类实体的parentCategory字段（即“正向端”）
    @ToString.Exclude // 避免循环toString
    @EqualsAndHashCode.Exclude // 避免循环equals
    private List<ArticleCategory> children = new ArrayList<>();

}
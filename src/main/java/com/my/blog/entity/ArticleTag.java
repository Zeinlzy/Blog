package com.my.blog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// ArticleTag.java
@Entity
@Table(name = "article_tag")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;
    
    @Column(nullable = false, unique = true, length = 50)
    private String tagName;
}
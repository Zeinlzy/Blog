package com.my.blog.dto.request;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class ArticleTagDeleteDTO {

    @Column(nullable = false, unique = true, length = 50)
    private String tagName;
}

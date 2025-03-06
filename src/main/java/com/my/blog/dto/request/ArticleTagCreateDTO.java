package com.my.blog.dto.request;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

@Data
public class ArticleTagCreateDTO {

    @Column(nullable = false, unique = true, length = 50)
    private String tagName;
}

package com.my.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ArticleCategoryDeleteDTO {

    @NotBlank(message = "分类名不能为空")
    @Size(max = 50, message = "分类名长度不能超过50个字符")
    private String categoryName;
}

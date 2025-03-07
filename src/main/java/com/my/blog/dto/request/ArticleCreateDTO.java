package com.my.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ArticleCreateDTO {
    @NotBlank(message = "文章标题不能为空")
    @Size(max = 200, message = "文章标题长度不能超过200个字符")
    private String title;

    @NotNull(message = "作者ID不能为空")
    private Long authorId;

    @NotBlank(message = "文章内容不能为空")
    private String content;

    @NotNull(message = "文章分类不能为空")
    private String categoryName;

    @NotEmpty(message = "至少需要一个标签")
    private List<Long> tagIds; // 新增标签ID列表
}
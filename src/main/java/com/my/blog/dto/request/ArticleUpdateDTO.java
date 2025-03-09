package com.my.blog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "文章更新DTO")
public class ArticleUpdateDTO {

    @Schema(description = "文章标题")
    @NotBlank(message = "文章标题不能为空")
    @Size(max = 100, message = "文章标题不能超过100个字符")
    private String title;

    @Schema(description = "文章内容")
    @NotBlank(message = "文章内容不能为空")
    private String content;

    @Schema(description = "文章摘要")
    @Size(max = 200, message = "文章摘要不能超过200个字符")
    private String summary;


    @Schema(description = "文章标签列表")
    private List<String> tags;
}
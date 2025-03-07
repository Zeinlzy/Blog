// ArticleTagRelationId.java
package com.my.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleTagRelationId implements Serializable {  // 必须是独立类
    private Long articleId;
    private Long tagId;
}
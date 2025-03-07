package com.my.blog.service;

import java.util.List;

public interface ArticleTagRelationService {
    void deleteByArticleIds(List<Long> articleIds);
}
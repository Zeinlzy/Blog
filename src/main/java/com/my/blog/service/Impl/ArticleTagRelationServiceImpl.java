package com.my.blog.service.Impl;

import com.my.blog.repository.ArticleTagRelationRepository;
import com.my.blog.service.ArticleTagRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleTagRelationServiceImpl implements ArticleTagRelationService {

    @Autowired
    private ArticleTagRelationRepository articleTagRelationRepository;

    @Override
    public void deleteByArticleIds(List<Long> articleIds) {
        if (articleIds != null && !articleIds.isEmpty()) {
            articleTagRelationRepository.deleteByArticleIds(articleIds);
        }
    }
}
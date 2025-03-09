package com.my.blog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.my.blog.dto.request.ArticleCategoryCreateDTO;
import com.my.blog.dto.request.ArticleUpdateDTO;
import com.my.blog.entity.Article;
import com.my.blog.entity.ArticleTag;
import com.my.blog.exception.CustomException;
import com.my.blog.exception.ErrorCode;
import com.my.blog.repository.ArticleRepository;
import com.my.blog.repository.ArticleTagRepository;
import com.my.blog.service.AdminArticleService;
import com.my.blog.service.ArticleTagRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminArticleServiceImpl implements AdminArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleTagRepository articleTagRepository;


    private final ArticleTagRelationService articleTagRelationService;

    @Override
    public IPage<Article> getAllArticles(int page, int size) {
        Page<Article> pageParam = new Page<>(page, size);
        return articleRepository.selectPage(pageParam, new QueryWrapper<Article>().orderByDesc("publish_time"));
    }

    @Override
    @Transactional
    public Article updateArticle(Long articleId, ArticleUpdateDTO articleUpdateDTO) {
        // 检查文章是否存在
        Article article = articleRepository.selectById(articleId);
        if (article == null) {
            throw new CustomException(ErrorCode.ARTICLE_NOT_FOUND);
        }

        // 更新文章基本信息
        article.setTitle(articleUpdateDTO.getTitle());
        article.setContent(articleUpdateDTO.getContent());
        article.setSummary(articleUpdateDTO.getSummary());
        article.setUpdateTime(LocalDateTime.now());
        
        // 保存文章
        articleRepository.updateById(article);
        
        // 处理标签
        if (articleUpdateDTO.getTags() != null && !articleUpdateDTO.getTags().isEmpty()) {
            // 先删除原有的标签关联
            articleRepository.deleteArticleTagRelations(articleId);
            
            // 添加新的标签关联
            List<ArticleTag> existingTags = new ArrayList<>();
            for (String tagName : articleUpdateDTO.getTags()) {
                // 查找标签是否存在
                ArticleTag tag = articleTagRepository.selectByName(tagName);
                if (tag == null) {
                    // 创建新标签
                    tag = new ArticleTag();
                    tag.setTagName(tagName);
                    articleTagRepository.insert(tag);

                    // 检查ID是否回填，如果没有回填则手动查询
                    if (tag.getTagId() == null) {
                        tag = articleTagRepository.selectByName(tagName);
                        if (tag == null) {
                            throw new CustomException(ErrorCode.OPERATION_FAILED);
                        }
                    }
                }
                existingTags.add(tag);
            }
            
            // 建立文章和标签的关联
            for (ArticleTag tag : existingTags) {
                articleRepository.insertArticleTagRelation(articleId, tag.getTagId());
            }
        }
        
        return article;
    }

    @Override
    @Transactional
    public void deleteArticle(Long articleId) {
        // 检查文章是否存在
        Article article = articleRepository.selectById(articleId);
        if (article == null) {
            throw new CustomException(ErrorCode.ARTICLE_NOT_FOUND);
        }
        
        // 删除文章标签关联
        articleRepository.deleteArticleTagRelations(articleId);
        
        // 删除文章
        articleRepository.deleteById(articleId);
    }

    @Override
    public void reviewArticle(Long articleId, boolean approved, String reason) {
        // 检查文章是否存在
        Article article = articleRepository.selectById(articleId);
        if (article == null) {
            throw new CustomException(ErrorCode.ARTICLE_NOT_FOUND);
        }
        
        // 更新文章状态
        article.setStatus(approved ? "approved" : "rejected");
        if (!approved && reason != null) {
            article.setRejectReason(reason);
        }
        
        articleRepository.updateById(article);
    }
}
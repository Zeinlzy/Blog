package com.my.blog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.my.blog.entity.ArticleTag;
import com.my.blog.repository.UserRepository;
import com.my.blog.service.ArticleTagRelationService;
import com.my.blog.utils.RedisUtils;
import com.my.blog.dto.request.ArticleCreateDTO;
import com.my.blog.entity.Article;
import com.my.blog.entity.ArticleCategory;
import com.my.blog.entity.ArticleTagRelation;
import com.my.blog.exception.CustomException;
import com.my.blog.exception.ErrorCode;
import com.my.blog.repository.ArticleCategoryRepository;
import com.my.blog.repository.ArticleRepository;
import com.my.blog.repository.ArticleTagRelationRepository;
import com.my.blog.repository.ArticleTagRepository;
import com.my.blog.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Transactional  //保持原子性
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleTagRepository articleTagRepository;
    private final UserRepository userRepository;
    private final ArticleTagRelationRepository articleTagRelationRepository;
    private final ArticleCategoryRepository articleCategoryRepository;

    @Autowired
    private ArticleTagRelationService articleTagRelationService;

    @Autowired
    private RedisUtils redisUtils; // 使用RedisUtils

    @Override
    public Article createArticle(ArticleCreateDTO articleDTO) {


        //创建文章前需要存在文章所属的分类
        // 分类存在性校验
        ArticleCategory category = articleCategoryRepository.findByName(articleDTO.getCategoryName());
        if (category == null){
            throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        Article article = Article.builder()
                .title(articleDTO.getTitle())
                .authorId(articleDTO.getAuthorId())
                .content(articleDTO.getContent())
                .publishTime(LocalDateTime.now())
                .build();

        articleRepository.insert(article);
        // 检查主键是否回填
        if (article.getArticleId() == null) {
            throw new RuntimeException("主键没回填");
        }

        // 处理标签关联
        processArticleTags(article, articleDTO.getTagIds());

        // 保存文章到Redis
        String redisKey = "article:" + article.getArticleId();
        redisUtils.set(redisKey, article,1, TimeUnit.HOURS);

        return article;


    }

    @Override
    public int deleteArticleByArticleId(Long articleId) {
        //先检查文章id是否存在，不存在抛出异常
        if(articleRepository.selectById(articleId) == null){
            throw new CustomException(ErrorCode.ARTICLE_NOT_FOUND);
        }

        //返回受影响的行数
        int result = articleRepository.deleteById(articleId);

        return result;
    }

    @Override
    public int deleteArticleByAuthorId(Long authorId) {

        //1.先检查作者id是否存在，不存在抛出异常
        if(userRepository.selectById(authorId) == null){
            throw new CustomException(ErrorCode.AUTHOR_NOT_FOUND);
        }

        //2.根据作者id查询出当前作者所发布的所有文章的id
        List<Long> articleIds = articleRepository.selectArticleIdsByAuthorId(authorId);

        // 2. 删除这些文章的标签关联
        if (!articleIds.isEmpty()) {
            articleTagRelationService.deleteByArticleIds(articleIds);
        }


        int result = articleRepository.deleteArticleByAuthorId(authorId);

        return result;

    }

    @Override
    public List selectArticleByTitle(String title) {
        List<Article> articles = articleRepository.selectArticleByTitle(title);

        if (articles == null){
            throw new CustomException(ErrorCode.ARTICLE_NOT_FOUND);
        }

        return articles;
    }


    private void processArticleTags(Article article, List<Long> tagIds) {
        tagIds.forEach(tagId -> {
            ArticleTag tag = articleTagRepository.findById(tagId);
            if (tag == null) {
                throw new CustomException(ErrorCode.TAG_NOT_FOUND);
            }

            ArticleTagRelation relation = new ArticleTagRelation();

            // 直接设置主键字段（非关联对象）
            relation.setArticleId(article.getArticleId());
            relation.setTagId(tag.getTagId());

            articleTagRelationRepository.insert(relation);
        });
    }

    @Override
    public IPage<Article> getAllArticles(int page, int size) {
        Page<Article> pageParam = new Page<>(page, size);
        return articleRepository.selectPage(pageParam, new QueryWrapper<Article>().orderByDesc("publish_time"));
    }

    // ... 现有代码 ...

    @Override
    public Article getArticleById(Long articleId) {
        // 先尝试从Redis缓存获取
        String redisKey = "article:" + articleId;
        Article article = (Article) redisUtils.get(redisKey);

        // 如果缓存中没有，则从数据库查询
        if (article == null) {
            article = articleRepository.selectById(articleId);

            // 如果数据库中存在该文章，则将其缓存到Redis
            if (article != null) {
                redisUtils.set(redisKey, article, 1, TimeUnit.HOURS);
            }
        }

        return article;
    }

// ... 现有代码 ...


}

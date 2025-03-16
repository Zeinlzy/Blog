package com.my.blog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    private ObjectMapper objectMapper;

    @Autowired
    private ArticleTagRelationService articleTagRelationService;

    @Autowired
    private RedisUtils redisUtils; // 使用RedisUtils

    @Override
    public Article createArticle(ArticleCreateDTO articleDTO) {

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
                .category(articleDTO.getCategoryName())
                .summary(generateSummary(articleDTO.getContent()))
                .status("draft")
                .build();

        int result = articleRepository.insert(article);
        if(result < 0){
            throw new CustomException(ErrorCode.OPERATION_FAILED);
        }
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

        //当文章不为草稿状态才能查询成功,查询不成功返回空列表
        List<Article> articles = articleRepository.selectList(
                new QueryWrapper<Article>()
                        .like("title",title) //模糊查询
                        .ne("status","draft") //ne:not equal
        );

        if (articles == null || articles.isEmpty()){
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
        return articleRepository.selectPage(pageParam,
                new QueryWrapper<Article>()
                        .ne("status","draft") //status not equal draft
                        .orderByDesc("publish_time"));
    }

    // ... 现有代码 ...

    @Override
    public Article getArticleById(Long articleId) {
        // 先尝试从Redis缓存获取
        String redisKey = "article:" + articleId;
        Object cached = redisUtils.get(redisKey);

        // 缓存命中，进行安全类型转换
        if (cached instanceof Article){
            return (Article) cached;
        }else if (cached != null){
            // 处理可能的旧格式缓存数据（如JSON反序列化为Map的情况）
            return convertToArticle(cached);
        }


        // 如果缓存中没有，则从数据库查询
        Article article = articleRepository.selectOne(
                new QueryWrapper<Article>()
                        .eq("article_id", articleId)
                        .ne("status", "draft")
        );

        // 统一处理空值
        if (article == null ) {
            throw new CustomException(ErrorCode.ARTICLE_NOT_FOUND);
        }

        // 更新缓存并增加阅读量
        incrementViewCount(articleId);
        redisUtils.set(redisKey, article, 1, TimeUnit.HOURS);

        return article;
    }

    @Override
    public void incrementViewCount(Long articleId) {
        articleRepository.incrementViewCount(articleId);
    }

    /**
     * 根据文章内容生成摘要
     * @param content 文章内容
     * @return 文章摘要
     */
    private String generateSummary(String content) {
        // 如果内容为空，返回空字符串
        if (content == null || content.isEmpty()) {
            return "";
        }

        // 取内容的前100个字符作为摘要，如果内容长度不足100，则取全部
        int summaryLength = Math.min(content.length(), 100);
        String summary = content.substring(0, summaryLength);

        // 如果摘要被截断，添加省略号
        if (content.length() > 100) {
            summary += "...";
        }

        return summary;
    }

    // 新增类型转换方法
    private Article convertToArticle(Object obj) {

        try {
            // 处理字符串类型
            if (obj instanceof String) {
                return objectMapper.readValue((String) obj, Article.class);
            }
            // 处理Map类型
            else if (obj instanceof Map) {
                return objectMapper.convertValue(obj, Article.class);
            }
            // 处理其他可能的类型
            else {
                // 尝试通过JSON序列化再反序列化
                String json = objectMapper.writeValueAsString(obj);
                return objectMapper.readValue(json, Article.class);
            }
        } catch (IOException e) {
            throw new CustomException(ErrorCode.CACHE_DATA_INVALID);
        }
    }
}

package com.my.blog.service.Impl;

import com.my.blog.entity.Article;
import com.my.blog.entity.User;
import com.my.blog.repository.ArticleRepository;
import com.my.blog.repository.UserRepository;
import com.my.blog.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热服务
 * 在应用启动时自动加载热点数据到Redis缓存
 */
@Slf4j
@Service
public class CacheWarmUpService implements ApplicationRunner {

    @Autowired
    private ArticleRepository articleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RedisUtils redisUtils;
    
    @Override
    public void run(ApplicationArguments args) {
        log.info("开始执行缓存预热...");
        
        // 预热热门文章
        warmUpHotArticles();
        
        // 预热活跃用户
        warmUpActiveUsers();
        
        // 预热系统配置
        warmUpSystemConfig();
        
        log.info("缓存预热完成");
    }
    
    /**
     * 预热热门文章
     */
    private void warmUpHotArticles() {
        try {
            log.info("开始预热热门文章...");
            // 获取热门文章，例如阅读量最高的前20篇
            List<Article> hotArticles = articleRepository.findTopNByViewCount(10);
            
            for (Article article : hotArticles) {
                String redisKey = "article:" + article.getArticleId();
                redisUtils.set(redisKey, article, 1, TimeUnit.HOURS);
                log.debug("已预热文章: {}", article.getTitle());
            }
            log.info("热门文章预热完成，共预热{}篇文章", hotArticles.size());
        } catch (Exception e) {
            log.error("热门文章预热失败", e);
        }
    }
    
    /**
     * 预热活跃用户
     */
    private void warmUpActiveUsers() {
        try {
            log.info("开始预热活跃用户...");
            // 获取活跃用户，例如最近登录的前5个用户
            List<User> activeUsers = userRepository.findTopNActiveUsers(5);
            
            for (User user : activeUsers) {
                String redisKey = "user:" + user.getId();
                redisUtils.set(redisKey, user, 2, TimeUnit.HOURS);
                log.debug("已预热用户: {}", user.getUsername());
            }
            log.info("活跃用户预热完成，共预热{}个用户", activeUsers.size());
        } catch (Exception e) {
            log.error("活跃用户预热失败", e);
        }
    }
    
    /**
     * 预热系统配置
     */
    private void warmUpSystemConfig() {
        try {
            log.info("开始预热系统配置...");
            // 这里可以加载系统配置到缓存
            // 例如：网站设置、全局参数等
            
            // 示例：假设有一个系统配置表
            // List<SystemConfig> configs = systemConfigRepository.findAll();
            // for (SystemConfig config : configs) {
            //     String redisKey = "config:" + config.getConfigKey();
            //     redisUtils.set(redisKey, config.getConfigValue(), 24, TimeUnit.HOURS);
            // }
            
            log.info("系统配置预热完成");
        } catch (Exception e) {
            log.error("系统配置预热失败", e);
        }
    }
}
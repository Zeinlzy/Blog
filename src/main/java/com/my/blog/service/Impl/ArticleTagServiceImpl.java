package com.my.blog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.my.blog.dto.request.ArticleTagCreateDTO;
import com.my.blog.entity.ArticleCategory;
import com.my.blog.entity.ArticleTag;
import com.my.blog.exception.CustomException;
import com.my.blog.exception.ErrorCode;
import com.my.blog.repository.ArticleTagRepository;
import com.my.blog.service.ArticleTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleTagServiceImpl implements ArticleTagService {

    private final ArticleTagRepository articleTagRepository;

    @Override
    public boolean findByName() {
        return false;
    }

    //创建标签
    @Override
    public ArticleTag createTag(ArticleTagCreateDTO articleTagCreateDTO) {

        if (articleTagRepository.findByName(articleTagCreateDTO.getTagName())){
            throw new CustomException(ErrorCode.TAG_ALREADY_EXISTS);
        }

        ArticleTag articleTag = ArticleTag.builder()
                .tagName(articleTagCreateDTO.getTagName())
                .build();


        //插入标签
        articleTagRepository.insert(articleTag);

        return articleTag;
    }

    //查询所有标签
    @Override
    public List<ArticleTag> selectAll() {
        return articleTagRepository.selectList(new QueryWrapper<>());

    }

    //删除标签
    @Override
    public int deleteByName(String tagName) {
        int result = articleTagRepository.deleteByName(tagName);

        if (result == 0){
            throw new CustomException(ErrorCode.TAG_NOT_FOUND);
        }
        return result;
    }

}

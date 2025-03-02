package com.my.blog.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.my.blog.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
//@Repository
public interface UserRepository extends BaseMapper<User> {

    //简单查询 → 注解（减少文件切换）
    //动态SQL → XML（利用标签优势）

    //用于明确表示一个<User>对象可能存在或不存在，从而避免直接返回 null 值引发的空指针异常
    @Select("SELECT * FROM user WHERE username = #{username}")
    Optional<User> selectByUsername(@Param("username") String username);

    @Select("SELECT COUNT(*) > 0 FROM user WHERE email = #{email}")
    boolean existsByEmail(@Param("email") String email);

    @Select("SELECT COUNT(*) > 0 FROM user WHERE username = #{username}")
    boolean existsByUsername(@Param("username") String username);
}

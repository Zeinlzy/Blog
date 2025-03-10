package com.my.blog.dto.response;

import lombok.Data;

/**
 * @Data 默认生成的构造方法
 * @Data 不会自动生成全参构造函数，它只会生成以下内容：
 * Getter / Setter（非 final 字段）
 * toString() / equals() / hashCode()
 * 无参构造函数（仅当类中没有显式定义任何构造函数时）
 */
@Data
public class TokenPair { //TokenPair的直译为令牌对
    private String accessToken; //访问令牌
    private String refreshToken;  //刷新令牌

    public TokenPair(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    /*
    1.使用场景流程
        用户登录 → 服务端生成 TokenPair 返回客户端
        访问资源 → 客户端携带 accessToken 请求 API
        令牌过期 → 当 accessToken 失效时，用 refreshToken 申请新令牌
        刷新机制 → 服务端验证 refreshToken 后返回新 TokenPair
    2.设计意义
        安全性：accessToken 短期有效减少泄露风险
        用户体验：refreshToken 自动续期避免重复登录
        权限控制：可随时通过刷新机制回收权限
*/
}
package com.tcm.repository;

import com.tcm.model.WechatUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 微信用户数据访问接口
 */
@Repository
public interface WechatUserRepository extends JpaRepository<WechatUser, Long> {
    
    /**
     * 根据OpenID查找用户
     */
    Optional<WechatUser> findByOpenid(String openid);
    
    /**
     * 根据OpenID更新用户最后登录时间和登录次数
     */
    @Query("UPDATE WechatUser w SET w.lastLoginAt = CURRENT_TIMESTAMP, w.loginCount = w.loginCount + 1 WHERE w.openid = :openid")
    void updateLastLoginAndCount(@Param("openid") String openid);
}
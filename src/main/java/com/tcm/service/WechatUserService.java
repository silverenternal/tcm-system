package com.tcm.service;

import com.tcm.model.WechatUser;
import com.tcm.repository.WechatUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 微信用户业务逻辑服务
 */
@Service
public class WechatUserService {
    
    @Autowired
    private WechatUserRepository wechatUserRepository;
    
    /**
     * 根据OpenID查找用户
     */
    public Optional<WechatUser> findByOpenid(String openid) {
        return wechatUserRepository.findByOpenid(openid);
    }
    
    /**
     * 保存或更新微信用户
     */
    public WechatUser save(WechatUser wechatUser) {
        return wechatUserRepository.save(wechatUser);
    }
    
    /**
     * 根据OpenID更新用户最后登录时间和登录次数
     */
    public void updateLastLoginAndCount(String openid) {
        wechatUserRepository.updateLastLoginAndCount(openid);
    }
}
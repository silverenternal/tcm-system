package com.tcm.test;

import com.tcm.TcmApplication;
import com.tcm.api.WechatMiniProgramAPI;
import com.tcm.service.WechatUserService;
import com.tcm.utils.WechatUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 测试微信小程序相关组件是否能被 Spring 容器正确加载
 */
@SpringBootTest(classes = {WechatMiniProgramAPI.class, WechatUserService.class, WechatUtil.class})
@ContextConfiguration(classes = TcmApplication.class)
public class WechatIntegrationTest {

    @Autowired
    private WechatMiniProgramAPI wechatMiniProgramAPI;

    @Autowired
    private WechatUserService wechatUserService;

    @Autowired
    private WechatUtil wechatUtil;

    @Test
    public void testComponentsWired() {
        // 验证组件是否被正确注入
        assertNotNull(wechatMiniProgramAPI, "WechatMiniProgramAPI should be wired");
        assertNotNull(wechatUserService, "WechatUserService should be wired");
        assertNotNull(wechatUtil, "WechatUtil should be wired");
        
        System.out.println("All WeChat components are correctly wired in Spring context.");
    }
}
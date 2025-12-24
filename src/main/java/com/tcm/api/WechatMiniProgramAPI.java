package com.tcm.api;

import com.tcm.model.WechatUser;
import com.tcm.service.WechatUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 微信小程序接口
 */
@RestController
@RequestMapping("/api/wechat")
public class WechatMiniProgramAPI {

    @Autowired
    private WechatUserService wechatUserService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // 这里需要实现微信登录逻辑，获取openid等信息
        // 并将用户信息保存到数据库
        return ResponseEntity.ok().build();
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/user/{openid}")
    public ResponseEntity<?> getUser(@PathVariable String openid) {
        // 根据openid从数据库获取用户信息
        return ResponseEntity.ok().build();
    }

    /**
     * 获取小程序码
     */
    @PostMapping("/getQrCode")
    public ResponseEntity<?> getQrCode(@RequestBody QrCodeRequest request) {
        // 生成并返回小程序码
        return ResponseEntity.ok().build();
    }

    /**
     * 消息推送
     */
    @PostMapping("/message")
    public ResponseEntity<?> message(@RequestBody MessageRequest request) {
        // 实现消息推送逻辑
        return ResponseEntity.ok().build();
    }

    // 登录请求体
    public static class LoginRequest {
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    // 小程序码请求体
    public static class QrCodeRequest {
        private String scene;
        private String page;

        public String getScene() {
            return scene;
        }

        public void setScene(String scene) {
            this.scene = scene;
        }

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }
    }

    // 消息推送请求体
    public static class MessageRequest {
        private String touser;
        private String msgtype;
        private Object content;

        public String getTouser() {
            return touser;
        }

        public void setTouser(String touser) {
            this.touser = touser;
        }

        public String getMsgtype() {
            return msgtype;
        }

        public void setMsgtype(String msgtype) {
            this.msgtype = msgtype;
        }

        public Object getContent() {
            return content;
        }

        public void setContent(Object content) {
            this.content = content;
        }
    }
}
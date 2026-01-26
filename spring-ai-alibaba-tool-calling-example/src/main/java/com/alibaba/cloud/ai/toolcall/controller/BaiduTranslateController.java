/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.cloud.ai.toolcall.controller;

import com.alibaba.cloud.ai.toolcalling.baidutranslate.BaiduTranslateService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 百度翻译控制器
 * <p>演示通过工具名称进行工具调用
 */
@RestController
@RequestMapping("/translate")
public class BaiduTranslateController {

    private final ChatClient dashScopeChatClient;


    public BaiduTranslateController(ChatClient chatClient, BaiduTranslateService baiduTranslateService) {

        this.dashScopeChatClient = chatClient;
    }

    /**
     * 普通对话接口（不使用工具）
     */
    @GetMapping("/chat")
    public String simpleChat(@RequestParam(value = "query", defaultValue = "帮我把以下内容翻译成英文：你好，世界。") String query) {

        return dashScopeChatClient.prompt(query).call().content();
    }

    /**
     * 工具调用接口（通过工具名称）
     * <p>通过 toolNames 指定要使用的工具名称，AI 会自动调用对应的翻译服务
     */
    @GetMapping("/chat-tool-function-callback")
    public String chatTranslateFunction(@RequestParam(value = "query", defaultValue = "帮我把以下内容翻译成英文：你好，世界。") String query) {

        return dashScopeChatClient.prompt(query)
                .toolNames("baiduTranslate")
                .call()
                .content();
    }

}

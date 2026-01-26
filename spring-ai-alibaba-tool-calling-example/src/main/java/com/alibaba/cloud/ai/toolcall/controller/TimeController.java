/*
 * Copyright 2024-2025 the original author or authors.
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

import com.alibaba.cloud.ai.toolcall.component.TimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 时间查询控制器
 * <p>演示普通对话和工具调用两种方式
 */
@RestController
@RequestMapping("/time")
public class TimeController {

    private final ChatClient dashScopeChatClient;

    private final TimeTools timeTools;

    public TimeController(ChatClient chatClient, TimeTools timeTools) {

        this.dashScopeChatClient = chatClient;
        this.timeTools = timeTools;
    }

    /**
     * 普通对话接口（不使用工具）
     */
    @GetMapping("/chat")
    public String simpleChat(@RequestParam(value = "query", defaultValue = "请告诉我现在北京时间几点了") String query) {

        return dashScopeChatClient.prompt(query).call().content();
    }

    /**
     * 工具调用接口（方法作为工具）
     * <p>AI 会根据问题自动调用 TimeTools 中的方法获取准确时间
     */
    @GetMapping("/chat-tool-method")
    public String chatWithTimeFunction(@RequestParam(value = "query", defaultValue = "请告诉我现在北京时间几点了") String query) {

        return dashScopeChatClient.prompt(query).tools(timeTools).call().content();
    }

}

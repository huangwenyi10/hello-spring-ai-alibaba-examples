/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.ai.example.outparser.controller;

import com.alibaba.cloud.ai.dashscope.api.DashScopeResponseFormat;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 结构化输出示例：使用 DashScope 原生 JSON 模式。
 * 通过 response_format=json_object 约束模型输出为合法 JSON。
 */
@RestController
@RequestMapping("/json")
public class JsonController {

    private final ChatClient chatClient;
    /** DashScope 响应格式配置，指定 JSON_OBJECT 模式 */
    private final DashScopeResponseFormat responseFormat;

    public JsonController(ChatClient.Builder builder) {
        DashScopeResponseFormat responseFormat = new DashScopeResponseFormat();
        responseFormat.setType(DashScopeResponseFormat.Type.JSON_OBJECT);

        this.responseFormat = responseFormat;
        this.chatClient = builder.build();
    }

    /** 普通调用，无格式约束，模型可能返回非标准 JSON */
    @GetMapping("/chat")
    public String simpleChat(@RequestParam(value = "query", defaultValue = "请以JSON格式介绍你自己") String query) {
        return chatClient.prompt(query).call().content();
    }

    /** 推荐用法：通过 options 注入 responseFormat，模型以 JSON_OBJECT 模式输出 */
    @GetMapping("/chat-format")
    public String simpleChatFormat(@RequestParam(value = "query", defaultValue = "请以JSON格式介绍你自己") String query) {
        return chatClient.prompt(query)
                .options(
                        DashScopeChatOptions.builder()
                                .withTopP(0.7)
                                .withResponseFormat(responseFormat)
                                .build()
                )
                .call().content();
    }
}

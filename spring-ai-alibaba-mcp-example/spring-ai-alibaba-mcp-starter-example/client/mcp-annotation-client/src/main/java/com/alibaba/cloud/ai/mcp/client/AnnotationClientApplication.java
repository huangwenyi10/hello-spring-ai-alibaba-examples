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

package com.alibaba.cloud.ai.mcp.client;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;

/**
 * MCP 注解客户端应用
 * <p>演示使用注解方式注册 MCP 客户端处理器，通过命令行交互使用 MCP 工具
 *
 * @author yingzi
 * @since 2025/10/22
 */
@SpringBootApplication
public class AnnotationClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnnotationClientApplication.class, args);
    }

    /**
     * 命令行交互程序
     * <p>启动后进入交互模式，可以输入问题，AI 会调用 MCP 工具回答
     *
     * @param chatClientBuilder ChatClient 构建器
     * @param tools 工具回调提供者，自动扫描并注册带 MCP 注解的工具
     * @param context Spring 应用上下文
     */
    @Bean
    public CommandLineRunner predefinedQuestions(ChatClient.Builder chatClientBuilder, ToolCallbackProvider tools,
                                                 ConfigurableApplicationContext context) {

        return args -> {
            // 构建 ChatClient，注册所有 MCP 工具
            var chatClient = chatClientBuilder
                    .defaultToolCallbacks(tools.getToolCallbacks()).build();

            // 打印可用工具列表
            System.out.println("Available tools:");
            for (ToolCallback toolCallback : tools.getToolCallbacks()) {
                System.out.println(">>> " + toolCallback.getToolDefinition().name());
            }
            
            // 命令行交互循环
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("\n>>> QUESTION: ");
                String userInput = scanner.nextLine();
                if (userInput.equalsIgnoreCase("exit")) {
                    break;
                }
                System.out.println("\n>>> ASSISTANT: " + chatClient.prompt(userInput).call().content());
            }
            scanner.close();
            context.close();
        };
    }
}

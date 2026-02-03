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

package com.alibaba.cloud.ai.mcp.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MCP 注解式服务器应用主类
 * <p>使用 Spring Boot 自动配置，自动扫描并注册带有 {@code @McpTool} 注解的工具方法
 * <p>基于 WebFlux 实现响应式 MCP 服务器
 *
 * @author yingzi
 * @since 2025/10/22
 */
@SpringBootApplication
public class AnnotationServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnnotationServerApplication.class, args);
    }
}

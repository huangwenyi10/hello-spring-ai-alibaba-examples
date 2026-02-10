/*
 * Copyright 2025-2026 the original author or authors.
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
 *
 * @author brianxiadong
 */
package com.alibaba.cloud.ai.mcp.samples.client;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	// 浏览器或上游系统把一个“北京天气怎么样”的问题丢给 MCP WebFlux Client；Client 通过 SSE 长连到 http://localhost:8080/sse/mcp；
	// 模型判断需要工具，就向 MCP Server 发起 tools/list，发现有 getWeatherForecastByLocation 和 getAirQuality 两个工具；
	// 然后发起 callTool 请求，带上北京的经纬度；Server 这边由 OpenMeteoService 调外部 OpenMeteo API
	// 或生成模拟 AQI 数据，把结果封装成文本，通过 SSE 流回 Client，最后再由 Chat 层把结果转成一段自然语言回复给用户。
	@Bean
	public CommandLineRunner predefinedQuestions(ChatClient.Builder chatClientBuilder, ToolCallbackProvider tools,
			ConfigurableApplicationContext context) {

		ToolCallback[] toolCallbacks = tools.getToolCallbacks();
		System.out.println("Available tools:");
		for (ToolCallback toolCallback : toolCallbacks) {
			System.out.println(">>> " + toolCallback.getToolDefinition().name());
		}

		return args -> {
			var chatClient = chatClientBuilder
					.defaultToolCallbacks(tools.getToolCallbacks())
					.build();

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

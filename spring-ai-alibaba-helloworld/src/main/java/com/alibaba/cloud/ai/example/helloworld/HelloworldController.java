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

package com.alibaba.cloud.ai.example.helloworld;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import reactor.core.publisher.Flux;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */
@RestController
@RequestMapping("/helloworld")
public class HelloworldController {

	private static final String DEFAULT_PROMPT = "你是一个博学的智能聊天助手，请根据用户提问回答！";

	private final ChatClient dashScopeChatClient;

	// 也可以使用如下的方式注入 ChatClient
	public HelloworldController(ChatClient.Builder chatClientBuilder) {

		this.dashScopeChatClient = chatClientBuilder
				// 设置默认系统提示词，定义 AI 助手的角色和行为规范
				.defaultSystem(DEFAULT_PROMPT)
				// 添加消息记忆 Advisor：使用 MessageWindowChatMemory 实现基于窗口的消息记忆
				// MessageWindowChatMemory 会维护一个固定大小的消息窗口，保留最近的对话历史
				.defaultAdvisors(
						MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build())
				// 添加简单日志 Advisor：记录对话过程中的关键信息，便于调试和问题排查
				.defaultAdvisors(new SimpleLoggerAdvisor())
				// 设置 ChatClient 中 ChatModel 的默认 Options 参数
				// DashScopeChatOptions 是 DashScope 模型特有的配置选项
				.defaultOptions(
						DashScopeChatOptions.builder()
								// topP 参数：核采样概率阈值，控制生成文本的多样性
								// 0.7 表示只考虑累积概率前 70% 的 token，平衡确定性和创造性
								.topP(0.7)
								.build()
				)
				// 构建最终的 ChatClient 实例
				.build();
	}

	/**
	 * ChatClient 简单调用
	 */
	@GetMapping("/simple/chat")
	public String simpleChat(@RequestParam(value = "query", defaultValue = "你好，很高兴认识你，能简单介绍一下自己吗？") String query) {
		// 使用 ChatClient 发送提示词，同步调用并返回完整响应内容
		return dashScopeChatClient.prompt(query).call().content();
	}

	/**
	 * ChatClient 流式调用
	 */
	@GetMapping("/stream/chat")
	public Flux<String> streamChat(@RequestParam(value = "query", defaultValue = "你好，很高兴认识你，能简单介绍一下自己吗？") String query, HttpServletResponse response) {
		// 设置响应字符编码为 UTF-8，确保中文正确显示
		response.setCharacterEncoding("UTF-8");
		// 使用 ChatClient 发送提示词，流式调用并返回响应内容流（Flux）
		return dashScopeChatClient.prompt(query).stream().content();
	}

	/**
	 * ChatClient 使用自定义的 Advisor 实现功能增强.
	 * eg:
	 * http://127.0.0.1:18080/helloworld/advisor/chat/123?query=你好，我叫jack，之后的会话中都带上我的名字
	 * 你好，jack！很高兴认识你。在接下来的对话中，我会记得带上你的名字。有什么想聊的吗？
	 * http://127.0.0.1:18080/helloworld/advisor/chat/123?query=我叫什么名字？
	 * 你叫jack呀。有什么事情想要分享或者讨论吗，jack？
	 *
	 * refer: https://docs.spring.io/spring-ai/reference/api/chat-memory.html#_memory_in_chat_client
	 */
	@GetMapping("/advisor/chat/{conversationId}")
	public Flux<String> advisorChat(
			HttpServletResponse response,
			@PathVariable String conversationId,
			@RequestParam String query
	) {
		// 设置响应字符编码为 UTF-8
		response.setCharacterEncoding("UTF-8");

		// 使用 ChatClient 发送提示词，并通过 Advisor 设置会话 ID
		// 会话 ID 用于区分不同的对话上下文，使 AI 能够记住同一会话中的历史消息
		return this.dashScopeChatClient.prompt(query)
				.advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
				.stream()
				.content();
	}

	/**
	 * ChatClient 新的聊天接口，支持流式输出和自定义 ChatOptions 配置
	 * eg:
	 * http://127.0.0.1:18080/helloworld/advisor/newChat?query=你好&topP=0.8&temperature=0.9
	 */
	@GetMapping("/advisor/newChat")
	public Flux<String> newChat(
			HttpServletResponse response,
			@RequestParam(value = "query", defaultValue = "你好，很高兴认识你，能简单介绍一下自己吗？") String query,
			@RequestParam(value = "topP", required = false) Double topP,
			@RequestParam(value = "temperature", required = false) Double temperature,
			@RequestParam(value = "maxTokens", required = false) Integer maxToken) {
		// 设置响应字符编码为 UTF-8
		response.setCharacterEncoding("UTF-8");

		// 构建 ChatOptions，用于自定义模型参数
		DashScopeChatOptions.DashScopeChatOptionsBuilder optionsBuilder = DashScopeChatOptions.builder();

		// 如果提供了 topP 参数，则设置核采样概率阈值（控制输出多样性）
		if (topP != null) {
			optionsBuilder.topP(topP);
		}
		// 如果提供了 temperature 参数，则设置温度参数（控制输出随机性）
		if (temperature != null) {
			optionsBuilder.temperature(temperature);
		}
		// 如果提供了 maxToken 参数，则设置最大生成 token 数量
		if (maxToken != null) {
			optionsBuilder.maxToken(maxToken);
		}

		// 使用 ChatClient 发送提示词，应用自定义选项，流式调用并返回响应内容流
		return this.dashScopeChatClient.prompt(query)
				.options(optionsBuilder.build())
				.stream()
				.content();
	}

}
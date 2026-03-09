package com.cloud.alibaba.ai.example.skills.skillsagentexample.controller;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.cloud.alibaba.ai.example.skills.skillsagentexample.agent.SkillsAgent;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.*;

// 对外暴露 HTTP 接口，接收请求并转发给 Skills Agent 处理
@RestController
public class ChatController {

    private final SkillsAgent skillsAgent;
    private final ChatModel chatModel;
    // 复用同一个 ReactAgent 实例，避免每次请求都重新构建
    private ReactAgent agent;

    // 构造函数注入 Agent 构建器和底层 ChatModel
    public ChatController(SkillsAgent skillsAgent, ChatModel chatModel) {
        this.skillsAgent = skillsAgent;
        this.chatModel = chatModel;
    }

    // 简单的聊天入口，message 为自然语言任务描述
    @PostMapping("/chat")
    public String chat(String message) throws GraphRunnerException {
            System.out.println("开始执行");
            // 首次请求时构建 Agent，后续复用
            if (agent == null) {
                agent = skillsAgent.buildAgent(chatModel);
            }
            // 调用 Agent 执行完整推理与工具调用流程
            return String.valueOf(agent.call(message));
    }
}

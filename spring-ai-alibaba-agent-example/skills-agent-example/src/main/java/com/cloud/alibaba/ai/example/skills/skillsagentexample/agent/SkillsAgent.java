package com.cloud.alibaba.ai.example.skills.skillsagentexample.agent;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.extension.tools.filesystem.ListFilesTool;
import com.alibaba.cloud.ai.graph.agent.extension.tools.filesystem.ReadFileTool;
import com.alibaba.cloud.ai.graph.agent.extension.tools.filesystem.WriteFileTool;
import com.alibaba.cloud.ai.graph.agent.hook.shelltool.ShellToolAgentHook;
import com.alibaba.cloud.ai.graph.agent.interceptor.skills.SkillsInterceptor;
import com.alibaba.cloud.ai.graph.agent.tools.ShellTool;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;

// 构建具备「技能目录 + 工具链」能力的 ReactAgent 的服务类
@Service
public class SkillsAgent {

    // 代理类自己的日志对象
    private static final Logger logger = LoggerFactory.getLogger(SkillsAgent.class);
    // 用户技能定义所在的目录，相对工程根路径
    private static final String SKILLS_DIR = "spring-ai-alibaba-agent-example/skills-agent-example/skills";

    /**
     * 基于传入的 ChatModel 构建一个具备技能、工具和 Hook 的 ReactAgent
     */
    public ReactAgent buildAgent(ChatModel chatModel) {
        // 计算技能目录的绝对路径，方便在任何工作目录下运行
        Path skillsPath = Path.of(SKILLS_DIR).toAbsolutePath();
        logger.info("Skills directory: {}", skillsPath);

        // 启动前强校验：技能目录不存在则直接失败，避免“空能力”代理
        if (!Files.exists(skillsPath)) {
            logger.error("Skills directory not found at: {}", skillsPath);
            throw new IllegalStateException("Skills directory not found");
        }

        // 打印技能目录下有哪些文件，便于排查加载问题
        logger.info("Skills directory exists, listing contents:");
        try {
            Files.list(skillsPath).forEach(p ->
                logger.info("  - {}", p.getFileName())
            );
        } catch (IOException e) {
            logger.error("Failed to list directory", e);
        }

        // 构建 SkillsInterceptor，负责扫描 skills 目录并加载技能
        SkillsInterceptor interceptor = SkillsInterceptor.builder()
            .userSkillsDirectory(skillsPath.toString())
            .autoScan(true)
            .build();

        logger.info("Skills loaded: {}", interceptor.getSkillCount());

        // 注册基础工具：读文件、写文件、列目录、执行 Shell 命令
        List<ToolCallback> tools = new ArrayList<>();
        tools.add(ReadFileTool.createReadFileToolCallback(ReadFileTool.DESCRIPTION));
        tools.add(WriteFileTool.createWriteFileToolCallback(WriteFileTool.DESCRIPTION));
        tools.add(ListFilesTool.createListFilesToolCallback(ListFilesTool.DESCRIPTION));
        tools.add( ShellTool.builder(System.getProperty("user.dir"))
            .build());

        // 为 Shell 工具增加 Hook，可以在调用前后做审计或控制
        ShellToolAgentHook hook = ShellToolAgentHook.builder()
            .shellToolName("shell")
            .build();

        // 拼装最终的 ReactAgent：模型 + Hook + 技能拦截器 + 工具集合
        return ReactAgent.builder()
            .name("skill-agent")
            .model(chatModel)
            .hooks(hook)
            .interceptors(interceptor)
            .tools(tools)
            .enableLogging(true)
            .build();
    }
}



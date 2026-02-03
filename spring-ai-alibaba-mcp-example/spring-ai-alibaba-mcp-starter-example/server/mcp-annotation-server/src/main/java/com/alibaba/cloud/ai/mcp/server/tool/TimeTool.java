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

package com.alibaba.cloud.ai.mcp.server.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间工具服务
 * <p>提供获取指定时区当前时间的 MCP 工具
 *
 * @author yingzi
 * @since 2025/10/22
 */
@Service
public class TimeTool {

    private static final Logger logger = LoggerFactory.getLogger(TimeTool.class);

    /**
     * 获取指定时区的当前时间
     * <p>通过 {@code @McpTool} 注解注册为 MCP 工具，可被 MCP 客户端调用
     *
     * @param timeZoneId 时区ID，例如 "Asia/Shanghai"
     * @return 格式化的时间字符串，包含时区信息
     */
    @McpTool(name = "getCityTime", description = "Get the time of a specified city.")
    public String  getCityTimeMethod(@McpToolParam(description = "Time zone id, such as Asia/Shanghai", required = true) String timeZoneId) {
        logger.info("The current time zone is {}", timeZoneId);
        return String.format("The current time zone is %s and the current time is " + "%s", timeZoneId,
                getTimeByZoneId(timeZoneId));
    }

    /**
     * 根据时区ID获取格式化的时间字符串
     *
     * @param zoneId 时区ID
     * @return 格式化的时间字符串，格式为 "yyyy-MM-dd HH:mm:ss z"
     */
    private String getTimeByZoneId(String zoneId) {
        // 获取时区对象
        ZoneId zid = ZoneId.of(zoneId);

        // 获取该时区的当前时间
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zid);

        // 定义时间格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

        // 格式化时间为字符串
        String formattedDateTime = zonedDateTime.format(formatter);

        return formattedDateTime;
    }
}

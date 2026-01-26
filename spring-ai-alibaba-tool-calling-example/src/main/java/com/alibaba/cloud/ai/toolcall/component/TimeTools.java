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
package com.alibaba.cloud.ai.toolcall.component;

import com.alibaba.cloud.ai.toolcalling.time.GetTimeByZoneIdService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 时间工具类
 * <p>提供获取指定时区时间的工具方法，供 AI 模型调用
 */
public class TimeTools {

    private final GetTimeByZoneIdService timeService;

    public TimeTools(GetTimeByZoneIdService timeService) {
        this.timeService = timeService;
    }

    /**
     * 获取指定时区的当前时间
     *
     * @param timeZoneId 时区 ID，例如 Asia/Shanghai
     * @return 格式化后的时间字符串
     */
    @Tool(description = "Get the time of a specified city.")
    public String getCityTime(@ToolParam(description = "Time zone id, such as Asia/Shanghai")
                                    String timeZoneId) {

        return timeService.apply(new GetTimeByZoneIdService.Request(timeZoneId)).description();
    }

}

package com.youyi.ai.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/01
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @RequestMapping("/check")
    public String check() {
        return "ok";
    }
}

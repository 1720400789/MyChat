package org.zj.mychat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 */
@SpringBootApplication(scanBasePackages = {"org.zj.mychat"})
public class MyChatCustomApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyChatCustomApplication.class, args);
    }
}

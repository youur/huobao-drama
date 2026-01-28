package com.huobao.drama;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cloud.openfeign.EnableFeignClients;
import java.io.File;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
public class HuobaoDramaApplication {
    public static void main(String[] args) {
        // --- 核心修复：在 Spring 启动前强制创建目录 ---
        String[] dirs = {"data", "data/storage", "logs", "data/storage/images", "data/storage/videos", "data/storage/merged"};
        for (String dir : dirs) {
            File f = new File(dir);
            if (!f.exists()) {
                f.mkdirs();
            }
        }
        // ------------------------------------------
        
        SpringApplication.run(HuobaoDramaApplication.class, args);
    }
}

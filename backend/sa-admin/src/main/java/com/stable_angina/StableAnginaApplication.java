package com.stable_angina;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 稳定性心绞痛患者随访管理系统 —— 后端唯一启动入口。
 *
 * <p>工程为 Maven 4 模块：sa-admin → sa-business → sa-system → sa-common，
 * 本模块不含任何业务代码，只负责装配与启动。</p>
 *
 * <p>本地启动：JAVA_HOME 需指向 JDK 21，端口 9003，context-path /stableanginaapi。</p>
 */
@SpringBootApplication(scanBasePackages = "com.stable_angina")
@MapperScan("com.stable_angina.**.mapper")
@EnableScheduling
public class StableAnginaApplication {

    public static void main(String[] args) {
        SpringApplication.run(StableAnginaApplication.class, args);
    }
}

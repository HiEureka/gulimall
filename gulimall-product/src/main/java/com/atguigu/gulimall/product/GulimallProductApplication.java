package com.atguigu.gulimall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 1、整合Mybatis-plus
 *      1）导入依赖，由于很多模块都需要使用，所以在common里面引入
 *         <dependency>
 *             <groupId>com.baomidou</groupId>
 *             <artifactId>mybatis-plus-boot-starter</artifactId>
 *             <version>3.2.0</version>
 *         </dependency>
 *         <dependency>
 *             <groupId>mysql</groupId>
 *             <artifactId>mysql-connector-java</artifactId>
 *             <version>8.0.17</version>
 *         </dependency>
 *         注意mysql版本和mysql-connection驱动之间的关系
 *      2）、配置相关信息，在application.yml
 *          1、配置数据源，mysql的url、username，password等
 *          2、配置mybatis-plus ，比如直接@MapperScan
 *                              映射文件.xml
 *
 */
@SpringBootApplication
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}

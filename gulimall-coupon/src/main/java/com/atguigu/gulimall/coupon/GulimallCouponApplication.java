package com.atguigu.gulimall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**配置中心不步骤
 *  1、引入依赖
 *     <dependency>
 *     <groupId>com.alibaba.cloud</groupId>
 *     <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
 *     </dependency>
 *  2、创建一个bootstrap.properties配置文件，并配置：
 *          spring.application.name=gulimall-coupon
 *          spring.cloud.nacos.config.server-addr=127.0.0.1:8848
 *  3、需要给配置中心添加一个叫数据集(Data Id) gulimall-coupon.properties。默认规则是应用名.properties
 *  4、给 应用名.properties 添加配置内容
 * @Value(${""})  @ConfigurationProperties也可以获取配置文件的内容
 *  5、再Controller开启动态刷新注解@RefreshScope
 *  如果配置中心和当前应用的配置文件中有相同的配置项，优先使用配置中心的配置
 */
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallCouponApplication.class, args);
    }

}

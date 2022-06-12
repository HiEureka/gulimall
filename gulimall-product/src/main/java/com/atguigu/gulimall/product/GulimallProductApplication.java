package com.atguigu.gulimall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

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
 *2、后端数据校验JSR303
 *  1）、给Bean添加校验注解并可以选择指定message信息：该包下的注解javax.validation.constraints
 *  2)、在controller中要校验的数据前面添加@Valid注解
 *      效果：效验错误以后会有默认的响应
 *  3）、给效验的bean后紧跟一个BindingResult 属性，就可以获取到校验的结果
 *  4）、分组校验(多场景的复杂校验)
 *      1）、@NotBlank(message = "品牌名必须填写",groups = {UpdateGroup.class,AddGroup.class})
 *      2）、在处理器方法参数前面加@Validated({AddGroup.class}) 即用属性的@Validated注解
 *  5）、自定义校验
 *      1、编写一个自定义的校验注解
 *      2、编写一个自定义的校验器
 *      3、关联自定义的校验器与校验注解
 * 3、统一的异常处理
 *      1、新建一个类添加@CtrollerAdvice(basePackages = "com.atguigu.gulimall.product.controller")，作为异常处理类
 *      2、使用@ExceptionHandler(value = MethodArgumentNotValidException.class)里面的value属性指定处理的异常类型
 *      3、默认没有指定分组的校验注解@NotBlank，在指定校验分组的情况下不生效
 */

@SpringBootApplication
@EnableDiscoveryClient //开启注册中心
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}

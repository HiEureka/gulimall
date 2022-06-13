package com.atguigu.gulimall.product;

//import com.fasterxml.jackson.annotation.JsonPropertyOrder;
//import com.aliyun.oss.*;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.atguigu.gulimall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
//import org.springframework.aop.scope.ScopedProxyUtils;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;

//import javax.annotation.Resource;
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallProductApplicationTests {
    @Autowired
    BrandService brandService;
    @Autowired
    CategoryService categoryService;

    @Test
    public void findParentPath(){
        Long[] path = categoryService.findCategoryPath(225L);
        log.info("完整路径path-------------->{}", Arrays.asList(path));//path----------->[2, 34, 225]
    }
    /*
    p63,取消了common里面的spring-cloud-starter-alicloud-oss，单独加入third-party中
    @Autowired //@Resource
    OSSClient ossClient;  //想没有红线可以使用@Resource
    @Test
    public void testUpload(){
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        //String endpoint = "https://oss-cn-beijing.aliyuncs.com";
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        //String accessKeyId = "LTAI4FwvfjSycd1APnuG9bjj";
        //String accessKeySecret = "O6xaxyiWfSIitcOkSuK27ju4hXT5H1";
        // 填写Bucket名称，例如examplebucket。
        //以上配置在引入spring-cloud-starter-alicloud-oss后，必须在配置文件中进行配置
        String bucketName = "gulimall-hello";
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        String objectName = "xiaomi.png";
        // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
        // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        String filePath= "E:\\JavaProjects\\Projects\\two(guigu_gulishangcheng)\\docs\\pics\\xiaomi.png";

        // 创建OSSClient实例。
        //引入spring-cloud-starter-alicloud-oss后直接使用对象注入OSSClient
        //OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            InputStream inputStream = new FileInputStream(filePath);
            // 创建PutObject请求。
            ossClient.putObject(bucketName, objectName, inputStream);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
*/

    @Test
     public void contextLoads() {
        BrandEntity entity = new BrandEntity();
        entity.setName("华为");
        entity.setDescript("中国企业");
        boolean result = brandService.save(entity);
        if (result){
            System.out.println("保存成功！");
        } else {
            System.out.println("保存失败，发生未知错误");
        }
    }
    @Test
    public void updateBrand() {
        BrandEntity entity = new BrandEntity();
        entity.setBrandId(1L);
        entity.setName("Iphone");
        entity.setDescript("美国企业");
        boolean result = brandService.updateById(entity);
        if (result){
            System.out.println("修改成功！");
        } else {
            System.out.println("修改失败，发生未知错误");
        }
    }

}

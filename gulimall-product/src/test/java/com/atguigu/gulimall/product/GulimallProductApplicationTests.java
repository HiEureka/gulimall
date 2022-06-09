package com.atguigu.gulimall.product;

//import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import org.junit.Test;
import org.junit.runner.RunWith;
//import org.springframework.aop.scope.ScopedProxyUtils;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

//import javax.annotation.Resource;
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallProductApplicationTests {
    @Autowired
    BrandService brandService;

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

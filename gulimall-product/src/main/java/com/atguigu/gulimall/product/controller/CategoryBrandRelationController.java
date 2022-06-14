package com.atguigu.gulimall.product.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.entity.CategoryBrandRelationEntity;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;

//当在品牌管理页面点击关联分类，
// 请求地址：http://localhost:88/api/product/categorybrandrelation/catelog/list?t=1655190073739&brandId=1

/**
 * 品牌分类关联
 *
 * @author hieureka
 * @email sqh_fendou@163.com
 * @date 2022-06-05 15:46:31
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 【获取品牌关联的分类列表】
     * 当在品牌管理页面点击关联分类，
     * 请求地址：http://localhost:88/api/product/categorybrandrelation/catelog/list?t=1655190073739&brandId=1
     * 响应数据:
     * {
     * 	"msg": "success",
     * 	"code": 0,
     * 	"data": [{
     * 		"catelogId": 0,
     * 		"catelogName": "string",
     *        }]
     * }
     */
    //@RequestMapping(value = ("/catelog/list"),method = RequestMethod.GET)
    @GetMapping("/catelog/list")
    //@RequiresPermissions("product:categorybrandrelation:list")
    public R catelogList(@RequestParam("brandId") Long brandId){
        List<CategoryBrandRelationEntity> data = new ArrayList<>();
        data = categoryBrandRelationService.list(
                //查询条件
                new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId)
        );

        return R.ok().put("data", data);//响应数据写的data
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:categorybrandrelation:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:categorybrandrelation:info")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 请求地址例如:
     * GET http://localhost:88/api/product/categorybrandrelation/catelog/list?t=1655191110959&brandId=1
     * 请求参数:{"brandId":1,"catelogId":2}
     * 响应数据:{"brandId":1,"catelogId":2}
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:categorybrandrelation:save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
        //我们向数据添加数据时，还要把brand_name和catelog_name存入。可以使用关联查询，但是一般不建议
        categoryBrandRelationService.saveDetail(categoryBrandRelation);
		//categoryBrandRelationService.save(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:categorybrandrelation:update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:categorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}

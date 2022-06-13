package com.atguigu.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author hieureka
 * @email sqh_fendou@163.com
 * @date 2022-06-05 15:46:31
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
    /**
     * 查出所有分类以及子分类，以树形结构组装起来
     */
    List<CategoryEntity> listWithTree();
    /**
     * 删除,批量删除
     * @RequesBody:获取请求体，必须发送POST请求
     * springMVC自动将请求体的数据（json）封装为对应的对象，这里是Long[]
     */
    void removeMenuByIds(List<Long> asList);


    //【根据表pms_attr_group里面的catelogId(就是pms_category表里面的catId)查询父分类id，并将自己的id和父分类id放入Long数组】
    /**
     * 找到catelogId的完整路径[父/子/孙]
     * @param catelogId
     * @return
     */
    Long[] findCategoryPath(Long catelogId);
}


package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.entity.CategoryBrandRelationEntity;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    //使用哪张表就要注入哪个dao
    /*方式一
    @Autowired
    private CategoryDao categoryDao;
    */
    /**方式二，因为ServiceImpl的泛型就是 CategoryDao extends BaseMapper<CategoryEntity ，所以可以直接使用其类里的baseMapper
     * 如下：
     * public class ServiceImpl<CategoryDao extends BaseMapper<CategoryEntity>, CategoryEntity> implements IService<T> {
     *     @Autowired
     *     protected CategoryDao baseMapper;
     * }
     */

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查出所有分类以及子分类，以树形结构组装起来
     */
    @Override
    public List<CategoryEntity> listWithTree() {
        //1 查询出所有的分类数据
        List<CategoryEntity> entities = baseMapper.selectList(null);
        /*
        Java 8新增的Stream，配合同版本出现的Lambda ，给我们操作集合（Collection）提供了极大的便利
        Stream流是JDK8新增的成员，允许以声明性方式处理数据集合，可以把Stream流看作是遍历数据集合的一个高级迭代器。
        Stream 是 Java8 中处理集合的关键抽象概念，它可以指定你希望对集合进行的操作，可以执行非常复杂的查找/筛选/过滤、排序、聚合和映射数据等操作。
        使用Stream API 对集合数据进行操作，就类似于使用 SQL 执行的数据库查询。也可以使用 Stream API 来并行执行操作。
        简而言之，Stream API 提供了一种高效且易于使用的处理数据的方式。
         */
        //2 组装成父子的树形结构
        //2.1找出所有的一级分类，在表中，所有的一级分类的parent_cid为0
        //以集合的流的方式进行处理数据
        List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == 0;
        }).map((menu)->{
            menu.setChildren(getChildren(menu,entities));
            return menu;
        }).sorted((menu1,menu2)->{
            return ((menu1.getSort())==null?0:menu1.getSort()) - ((menu2.getSort())==null?0:menu2.getSort());
        }).collect(Collectors.toList());
        return level1Menus;
    }
    /**
     *【递归】查找所有菜单的子菜单
     * @param root 一级菜单
     * @param all 所有菜单
     * @return
     */
    private List<CategoryEntity> getChildren(CategoryEntity root,List<CategoryEntity> all){
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map((categoryEntity)->{
            //1 找到子菜单
            categoryEntity.setChildren(getChildren(categoryEntity,all));
            return categoryEntity;
        }).sorted((menu1,menu2)->{
            //2 菜单排序
            return ((menu1.getSort())==null?0:menu1.getSort()) - ((menu2.getSort())==null?0:menu2.getSort());
        }).collect(Collectors.toList());
        return children;
    }
    /**
     * 删除,批量删除
     * @RequesBody:获取请求体，必须发送POST请求
     * springMVC自动将请求体的数据（json）封装为对应的对象，这里是Long[]
     */
    @Override
    public void removeMenuByIds(List<Long> asList) {
    //TODO 检查当前删除的菜单，是否被别的地方引用
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCategoryPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        //查询出当前catelogId（catId）对应的当前分类
        //CategoryEntity byId = this.getById(catelogId);
        //查询出当前分类是否有父分类并查出父分类的信息
        //if (byId.getParentCid() != 0){
            //递归查询

        //}
        List<Long> parentPath = findParentPath(catelogId, paths);
        //逆序集合
        Collections.reverse(parentPath);
        return (Long[])parentPath.toArray(new Long[parentPath.size()]);
    }


    /**
     * //级联更新所有关联的数据
     * @param category
     */
    @Transactional  //事务
    @Override  //p75
    public void updateCascade(CategoryEntity category) {
        //先更新自己
        this.updateById(category);
        if (!StringUtils.isEmpty(category.getName())){
            //更新pms_category_brand_relation，注入service
            categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
            //TODO
        }

    }

    //递归查询父分类
    private List<Long> findParentPath(Long catelogId,List<Long> paths){
        //先将当前分类id放入集合
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        //查询出当前分类是否有父分类并查出父分类的信息
        if (byId.getParentCid() != 0){
        //递归查询
            findParentPath(byId.getParentCid(),paths);
        }
        return paths;
    }
}
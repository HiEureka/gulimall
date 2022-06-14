package com.atguigu.gulimall.product.service.impl;


import com.atguigu.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.AttrAttrgroupRelationService;
import com.atguigu.gulimall.product.vo.AttrRespVo;
import com.atguigu.gulimall.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.AttrDao;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.w3c.dom.Attr;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {
    //@Autowired
    //AttrAttrgroupRelationService attrAttrgroupRelationService;
    //老师用的时Dao
    @Autowired
    AttrAttrgroupRelationDao relationDao;  //关联关系
    @Autowired
    AttrGroupDao attrGroupDao;  //分组
    @Autowired
    CategoryDao categoryDao;  //分类

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveAttr(AttrVo attr) {
        //肯定会利用spring自带的可以复制对象属性的方法

        //首先，先将基本的属性信息存放到pms_attr表中
        AttrEntity attrEntity = new AttrEntity();
        //attrEntity.setAttrName(attr.getAttrName());利用spring自带的可以复制对象属性的方法
        BeanUtils.copyProperties(attr,attrEntity);//要保证属性名是一致的
        //1、保存基本数据
        this.save(attrEntity);
        //其次将属性与属性分组的信息（关联信息）存放到pms_attr_attrgroup_relation
        //AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
        //真神奇了。attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());这样设置attrid数据库就没有...
        //attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());
        ////attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
        //attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
        //boolean save = attrAttrgroupRelationService.save(attrAttrgroupRelationEntity);
        //if (save){
        //    System.out.println("保存成功");
        //}else {
        //    System.out.println("保存失败");
        //}
        //老师用的是Dao
        //其次将属性与属性分组的信息（关联信息）存放到pms_attr_attrgroup_relation
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrGroupId(attr.getAttrGroupId());
        relationEntity.setAttrId(attrEntity.getAttrId());
        relationDao.insert(relationEntity);
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId) {

        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>();
        if (catelogId != 0){
            queryWrapper.eq("catelog_id",catelogId);
        }
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            //模糊查询attr_id 或 attr_name
            queryWrapper.and((wrapper->{
                wrapper.like("attr_id",key).or().like("attr_name",key);
            }));
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrRespVo> respVos = records.stream().map((attrEntity -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);
            //1、设置分类和分组的名字
            AttrAttrgroupRelationEntity attrId = relationDao.selectOne(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
            if (attrId != null) {
                Long attrGroupId = attrId.getAttrGroupId();
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);
                attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
            }
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName());
            }
            return attrRespVo;
        })).collect(Collectors.toList());
        pageUtils.setList(respVos);
        return pageUtils;
    }

}
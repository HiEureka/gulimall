package com.atguigu.gulimall.product.service.impl;


import com.atguigu.common.constant.ProductConstant;
import com.atguigu.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.AttrGroupRelationVo;
import com.atguigu.gulimall.product.vo.AttrRespVo;
import com.atguigu.gulimall.product.vo.AttrVo;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
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

/**
 * 0代表销售属性，1代表基本属性(规格参数)
 * 只有【基本属性】的时候我们才会向关系表pms_attr_attrgroup_relation添加分组信息
 * 只有【销售属性】的时候我们不会向关系表pms_attr_attrgroup_relation添加分组信息
 */

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
    @Autowired
    CategoryService categoryService;//分类，在P78注入service而不是直接用dao
                                        // 是为了使用里面的递归查找父分类id的方法Long[] findCategoryPath(Long catelogId);
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

        /**************************

        //老师用的是Dao
        //其次将属性与属性分组的信息（关联信息）存放到pms_attr_attrgroup_relation
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrGroupId(attr.getAttrGroupId());
        relationEntity.setAttrId(attrEntity.getAttrId());
        relationDao.insert(relationEntity);
         **************************************/
        //老师用的是Dao
        //其次将属性与属性分组的信息（关联信息）存放到pms_attr_attrgroup_relation
        //只有是【规格参数的新增时】才会添加分组信息
        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());
            relationDao.insert(relationEntity);
        }
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type) {

        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>()
                .eq("attr_type","base".equalsIgnoreCase(type)?ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode():ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
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
            //AttrAttrgroupRelationEntity attrId = relationDao.selectOne(
            //        new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
            //if (attrId != null) {
            //    Long attrGroupId = attrId.getAttrGroupId();
            //    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);
            //    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
            //}
            if ("base".equalsIgnoreCase(type)){
                AttrAttrgroupRelationEntity attrId = relationDao.selectOne(
                        new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
                if (attrId != null) {
                    Long attrGroupId = attrId.getAttrGroupId();
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
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
    //因为没有那两个字段，所以使用我们自定义的AttrRespVo对象，想办法封装上那两个字段
    @Override
    @Transactional
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrRespVo respVo = new AttrRespVo();
        //先查出AttrEntity实体类，即pms_attr表中所有的信息
        AttrEntity attrEntity = this.getById(attrId);
        //将信息拷贝进响应对象，下面再将没有的那两个字段查出来，设置进respVo
        BeanUtils.copyProperties(attrEntity,respVo);
        //"attrGroupId" : 1 和  "catelogPath": [2, 34, 225]

        // 1、设置分组信息，relationDao
        //其中attrGroupId可以由attrId在关联表pms_attr_attrgroup_relation中查询到
        //只有是【规格参数（基本属性）的时】才会设置分组信息
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = relationDao.selectOne(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if (attrAttrgroupRelationEntity != null) {
                //设置attrGroupId
                respVo.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
                //顺便有了attrGroupId就把attr_group_name也设置了，虽然用不到  表: pms_attr_group  DAO :AttrGroupDao
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                if (attrGroupEntity != null) {
                    respVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }
        //2、设置分类信息 ，
        //catelogPath，利用上面查到的attrGroupId在pms_attr_group表中可以查到catelog_id，DAO :AttrGroupDao <--AttrEntity里面有catelog_id
        // 再利用catelog_id在pms_category表中递归查询出cat_id（parent_cid）即可   DAO : CategoryDao
        //不用不用，AttrEntity 里面有catelog_id 害，
        Long catelogId = attrEntity.getCatelogId();
        Long[] categoryPath = categoryService.findCategoryPath(catelogId);
        respVo.setCatelogPath(categoryPath);
        //顺便把名字也设置了
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        if (categoryEntity != null){
            respVo.setCatelogName(categoryEntity.getName());
        }
        return respVo;
    }

    @Override
    @Transactional
    public void updateAttr(AttrVo attr) {
        //先修改基本信息
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        this.updateById(attrEntity);

        //修改分组关联pms_attr_attrgroup_relation表
        //即由attr_id 修改新的attr_group_id
        /*
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrGroupId(attr.getAttrGroupId());
        relationEntity.setAttrId(attr.getAttrId());
        relationDao.update(relationEntity,
                new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attr.getAttrId()));
         */
        //修改分组关联pms_attr_attrgroup_relation表
        //即由attr_id 修改新的attr_group_id
        //只有是【规格参数的新增时】才会修改分组信息
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attr.getAttrId());
            Integer count = relationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            if (count > 0) {
                //修改操作
                relationDao.update(relationEntity,
                        new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            } else {
                //添加操作
                relationDao.insert(relationEntity);
            }
        }
    }

    /**
     * 根据分组id查找关联的所有基本属性
     * @param attrgroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> entities = relationDao.selectList(
                new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id",attrgroupId)
        );
        List<Long> attrIds = entities.stream().map((attr)->{
            return attr.getAttrId();
        }).collect(Collectors.toList());
        Collection<AttrEntity> attrEntities = this.listByIds(attrIds);
        return (List<AttrEntity>) attrEntities;
    }

    @Override
    public void deleteRelation(AttrGroupRelationVo[] vos) {
        //relationDao.delete(new QueryWrapper<>().eq("attr_id",1L).eq("attr_group_id",1L);)
        //批量删除 IN (1,2,3) in不对
        List<AttrAttrgroupRelationEntity> entities =
                Arrays.asList(vos).stream().map((item)->{
                    //item 就是AttrGroupRelationVo
                    AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
                    BeanUtils.copyProperties(item,relationEntity);
                    return relationEntity;
                }).collect(Collectors.toList());
        relationDao.deleteBatchRelation(entities);
    }

}
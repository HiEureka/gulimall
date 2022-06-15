package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.vo.AttrRespVo;
import com.atguigu.gulimall.product.vo.AttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.AttrEntity;

import java.util.Map;

/**
 * 商品属性
 *
 * @author hieureka
 * @email sqh_fendou@163.com
 * @date 2022-06-05 15:46:31
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);
    //保存，我们的AttrVo对象
    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId);
    //因为没有那两个字段，所以使用我们自定义的AttrRespVo对象，想办法封装上那两个字段
    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);
}


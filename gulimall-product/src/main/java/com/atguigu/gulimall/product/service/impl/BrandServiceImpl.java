package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.dao.CategoryBrandRelationDao;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.BrandDao;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {
    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<BrandEntity> wrapper = new QueryWrapper<BrandEntity>();
        //参数：Map<String, Object> params 是RenRenGenerator自动生成的前后端查询带分页
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){//说明品牌管理查询时，后台管理系统传递来了key
            //我们认为这个key可以是商品id，也可以是name
            wrapper.eq("brand_id",key).or().like("name",key);
        }
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                wrapper
        );

        /*
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                new QueryWrapper<BrandEntity>()
        );
        */

        return new PageUtils(page);
    }

    @Override
    @Transactional  //事务
    public void updateDetail(BrandEntity brand) {
        //先更新自己表的数据
        this.updateById(brand);
        //保证其他使用了本表的数据也要更新
        //更新冗余字段的数据保证一致
        if (!StringUtils.isEmpty(brand.getName())){
            //我觉得如果名称没改就没必要修改了，所以最好先判断一下，不过好像没修改就不会传过来
            //同步更新其他关联表中的数据
            categoryBrandRelationService.updateBrand(brand.getBrandId(),brand.getName());

            //TODO 更新其他关联表
        }
    }

}
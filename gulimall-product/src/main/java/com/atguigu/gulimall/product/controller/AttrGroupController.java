package com.atguigu.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.service.AttrService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.AttrGroupRelationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.service.AttrGroupService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;

//点击【属性分组】 --> [关联] 自动请求http://localhost:88/api/product/attrgroup/1/attr/relation?t=1655299821647
//                                                  即   /product/attrgroup/{attrgroupId}/attr/relation

/**
 * 属性分组
 *
 * @author hieureka
 * @email sqh_fendou@163.com
 * @date 2022-06-05 15:46:31
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    //三级分类查询service，表pms_category
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrService attrService;
    /**12、删除属性与分组的关联关系
     * 当点击  平台属性  -> 属性分组 -> 关联 -> 移除 请求地址/product/attrgroup/attr/relation/delete
     * 请求参数:[{"attrId":1,"attrGroupId":2}]
     * 是数组 如批量删除两个  [{attrId: 7, attrGroupId: 3}, {attrId: 10, attrGroupId: 3}]
     * 0: {attrId: 7, attrGroupId: 3}
     * 1: {attrId: 10, attrGroupId: 3}
     * 响应数据:
     * {
     * 	"msg": "success",
     * 	"code": 0
     * }
     *
     */
    @PostMapping("/attr/relation/delete")
    public R deleteRlation(@RequestBody AttrGroupRelationVo[] vos){  //请求参数是数组，建立一个Vo专门封装attrId和attrGroupId请求参数
        attrService.deleteRelation(vos);
        return R.ok();
    }

    /**
     * 平台属性  -> 属性分组 的关联
     * 请求地址: /product/attrgroup/{attrgroupId}/attr/relation
     * 需要查询attr即属性信息，所以要注入AttrService来查询属性信息
     * 响应参数:
     * {
     *   "msg": "success",
     *   "code": 0,
     *   "data": [
     *     {
     *       "attrId": 4,
     *       "attrName": "aad",
     *       "searchType": 1,
     *       "valueType": 1,
     *       "icon": "qq",
     *       "valueSelect": "v;q;w",
     *       "attrType": 1,
     *       "enable": 1,
     *       "catelogId": 225,
     *       "showDesc": 1
     *     }
     *   ]
     * }
     */
    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId){
        List<AttrEntity> entities = attrService.getRelationAttr(attrgroupId);
        return R.ok().put("data",entities);
    }
    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable ("catelogId")  Long catelogId){
        //PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.queryPage(params,catelogId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);//pms_attr_group表
        Long catelogId = attrGroup.getCatelogId();
        Long[] categoryPath = categoryService.findCategoryPath(catelogId); //pms_attr_group表的catelogId就是pms_category的catId
        attrGroup.setCatelogPath(categoryPath);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}

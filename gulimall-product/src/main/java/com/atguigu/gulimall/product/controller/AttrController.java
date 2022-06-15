package com.atguigu.gulimall.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.atguigu.gulimall.product.vo.AttrRespVo;
import com.atguigu.gulimall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.service.AttrService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;

//平台属性-->规格参数里面的新增    请求地址http://localhost:88/api/product/attr/save
//平台属性-->规格参数 进入该页面自动发送请求  http://localhost:88/api/product/attr/base/list/0?t=1655212407322&page=1&limit=10&key=
//平台属性-->规格参数 选择三级菜单中的手机->手机通讯->手机 发送请求  http://localhost:88/api/product/attr/base/list/225?t=1655212631188&page=1&limit=10&key=
//平台属性-->规格参数-->修改， 数据回显  发送请求 http://localhost:88/api/product/attr/info/10?t=1655260008942
//                                        即http://localhost:88/api/product/attr/info/{attr_id}?t=1655260008942

//平台属性-->规格参数-->修改， 数据回显-->填入要修改数据 --> 确认，发送请求 http://localhost:88/api/product/attr/update
/**
 * 商品属性
 *
 * @author hieureka
 * @email sqh_fendou@163.com
 * @date 2022-06-05 15:46:31
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    //请求地址base/list/0?t=1655212407322&page=1&limit=10&key=
    //请求地址base/list/225?t=1655212631188&page=1&limit=10&key=
    /**
     * 列表
     * 请求参数{
     *    page: 1,//当前页码
     *    limit: 10,//每页记录数
     *    sidx: 'id',//排序字段
     *    order: 'asc/desc',//排序方式
     *    key: '华为'//检索关键字
     * }
     * 响应数据{
     * 	"msg": "success",
     * 	"code": 0,
     * 	"page": {
     * 		"totalCount": 0,
     * 		"pageSize": 10,
     * 		"totalPage": 0,
     * 		"currPage": 1,
     * 		"list": [{
     * 			"attrId": 0, //属性id
     * 			"attrName": "string", //属性名
     * 			"attrType": 0, //属性类型，0-销售属性，1-基本属性
     * 			"catelogName": "手机/数码/手机", //所属分类名字
     * 			"groupName": "主体", //所属分组名字
     * 			"enable": 0, //是否启用
     * 			"icon": "string", //图标
     * 			"searchType": 0,//是否需要检索[0-不需要，1-需要]
     * 			"showDesc": 0,//是否展示在介绍上；0-否 1-是
     * 			"valueSelect": "string",//可选值列表[用逗号分隔]
     * 			"valueType": 0//值类型[0-为单个值，1-可以选择多个值]
     *                }]* 	}
     * }
     */
    @RequestMapping("/base/list/{catelogId}")
    //@RequiresPermissions("product:attr:list")
    public R baseAttrList(@RequestParam Map<String, Object> params,
                          @PathVariable("catelogId") Long catelogId){
        PageUtils page = attrService.queryBaseAttrPage(params,catelogId);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**发送请求 http://localhost:88/api/product/attr/info/10?t=1655260008942
     * 即http://localhost:88/api/product/attr/info/{attr_id}?t=1655260008942
     * 相应数据{
     * 	"msg": "success",
     * 	"code": 0,
     * 	"attr": {
     * 		"attrId": 4,
     * 		"attrName": "aad",
     * 		"searchType": 1,
     * 		"valueType": 1,
     * 		"icon": "qq",
     * 		"valueSelect": "v;q;w",
     * 		"attrType": 1,
     * 		"enable": 1,
     * 		"showDesc": 1,
     * 		"attrGroupId": 1, //分组id《--pms_attr没有该字段
     * 		"catelogId": 225, //分类id
     * 		"catelogPath": [2, 34, 225] //分类完整路径《--pms_attr没有该字段
     *        }
     * }
     *因为没有那两个字段，所以使用我们自定义的AttrRespVo对象，想办法封装上那两个字段
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
		//AttrEntity attr = attrService.getById(attrId);
        AttrRespVo respVo = attrService.getAttrInfo(attrId);

        return R.ok().put("attr", respVo);
    }

    /**
     * 保存
     */
    /*
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrEntity attr){
		attrService.save(attr);

        return R.ok();
    }
    */
    /**
     * 保存，利用我们的AttrVo对象
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:atr:save")
    public R save(@RequestBody AttrVo attr){
        attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 发送请求 http://localhost:88/api/product/attr/update
     * 请求参数:{
     *   "attrId": 0, //属性id
     *   "attrGroupId": 0, //属性分组id《---pms_attr表（或AttrEntity）没有，AttrVo有
     *   "attrName": "string",//属性名
     *   "attrType": 0, //属性类型
     *   "catelogId": 0, //分类id
     *   "enable": 0, //是否可用
     *   "icon": "string", //图标
     *   "searchType": 0, //是否检索
     *   "showDesc": 0, //快速展示
     *   "valueSelect": "string", //可选值列表
     *   "valueType": 0 //可选值模式 《---pms_attr表（或AttrEntity）没有，AttrVo没有
     * }
     * 响应数据:{
     * 	"msg": "success",
     * 	"code": 0
     * }
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attr){
        attrService.updateAttr(attr);

        return R.ok();
    }
    /*自带的修改
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrEntity attr){
		attrService.updateById(attr);

        return R.ok();
    }
    */
    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}

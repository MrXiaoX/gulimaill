package com.mrxiao.gulimall.product.app;

import com.mrxiao.common.utils.PageUtils;
import com.mrxiao.common.utils.R;
import com.mrxiao.gulimall.product.entity.AttrEntity;
import com.mrxiao.gulimall.product.entity.AttrGroupEntity;
import com.mrxiao.gulimall.product.service.AttrAttrgroupRelationService;
import com.mrxiao.gulimall.product.service.AttrGroupService;
import com.mrxiao.gulimall.product.service.AttrService;
import com.mrxiao.gulimall.product.service.CategoryService;
import com.mrxiao.gulimall.product.vo.AttrGroupRelationVo;
import com.mrxiao.gulimall.product.vo.AttrGroupWithAttrsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 属性分组
 *
 * @author mrxiao
 * @email ismrxiao@163.com
 * @date 2020-05-12 19:47:03
 */
@RestController
@RequestMapping("product/attrgroup")
@Slf4j
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;


    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    //product/attrgroup/{catelogId}/withattr
    @GetMapping("/{catelogId}/withattr")
    public R getAttrgroupWithattrAttr(@PathVariable("catelogId")Long catelogId ){
        //1、查出当前分类下的所有属性分组，
        //2、查出每个属性分组的所有属性
        List<AttrGroupWithAttrsVo> vos =  attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);
        return R.ok().put("data",vos);
    }

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
//    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,@PathVariable("catelogId") Long catelogId){
//        PageUtils page = attrGroupService.queryPage(params);

        PageUtils page = attrGroupService.queryPage(params,catelogId);
        System.out.println(page);

        return R.ok().put("page", page);
    }

//    product/attrgroup/attr/relation
    @PostMapping("/attr/relation")
    public R addRelation(@RequestBody List<AttrGroupRelationVo> vos){
        attrAttrgroupRelationService.saveRelation(vos);
        return R.ok();
    }

    //product/attrgroup/attr/relation/delete
    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrGroupRelationVo[] vos){
         attrService.deleteRelation(vos);
        return R.ok();
    }

    ///product/attrgroup/{attrgroupId}/noattr/relation
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R attrNoRelation(@PathVariable("attrgroupId") Long attrgroupId,Map<String, Object> params){
        PageUtils pageUtils=attrService.getNoRelationAttr(params,attrgroupId);
        return R.ok().put("page",pageUtils);
    }

    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId){
        List<AttrEntity> attrRelationList=attrService.getAttrRelation(attrgroupId);
        return R.ok().put("data",attrRelationList);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
//    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        log.info("attrGroupId{}",attrGroup);
        System.out.println("attrGroupId"+attrGroup);
        Long catelogId = attrGroup.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        System.out.println("catelogPath{}"+Arrays.toString(catelogPath));
        attrGroup.setCatelogPath(catelogPath);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
//    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
        attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
//    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
        attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
//    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}

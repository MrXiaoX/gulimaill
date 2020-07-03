package com.mrxiao.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrxiao.common.utils.PageUtils;
import com.mrxiao.common.utils.Query;
import com.mrxiao.gulimall.product.dao.CategoryDao;
import com.mrxiao.gulimall.product.entity.CategoryEntity;
import com.mrxiao.gulimall.product.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

//    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    public List<CategoryEntity> listWithTree() {
        System.out.println("★★★用户卡类Service#");
        //1.查出所有分类
        List<CategoryEntity> categoryEntity = baseMapper.selectList(null);
        //2.组装成父子结构
        //2.1 找到所有的一级分类
        List<CategoryEntity> level1Menus = categoryEntity.parallelStream()
                .filter(categoryEntity1 -> categoryEntity1.getParentCid() == 0)
                .map(categoryEntity1 -> {
                    categoryEntity1.setChildren(getChildrens(categoryEntity1,categoryEntity));
                    return categoryEntity1;
                }).sorted((m1, m2) ->{
                    return (m1.getSort()==null?0:m1.getSort()) - (m2.getSort()==null?0:m2.getSort());
                })
                .collect(Collectors.toList());


        System.out.println("★★★用户卡类结果#"+categoryEntity);
        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 1.检查当前删除的菜单，是否被其他引用
        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    private List<CategoryEntity> getChildrens(CategoryEntity root,List<CategoryEntity> all){
        List<CategoryEntity> collect = all.parallelStream()
                .filter(categoryEntity ->categoryEntity.getParentCid() == root.getCatId())
                //找到子菜单
                .map(categoryEntity ->{
                    categoryEntity.setChildren(getChildrens(categoryEntity, all));
                    return categoryEntity;
                    //菜单排序
                }).sorted((m1, m2) ->{
                    return (m1.getSort()==null?0:m1.getSort()) - (m2.getSort()==null?0:m2.getSort());
                }).collect(Collectors.toList());
        return collect;
    }
}
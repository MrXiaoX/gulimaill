package com.mrxiao.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrxiao.common.utils.PageUtils;
import com.mrxiao.common.utils.Query;
import com.mrxiao.gulimall.product.dao.CategoryDao;
import com.mrxiao.gulimall.product.entity.CategoryEntity;
import com.mrxiao.gulimall.product.service.CategoryBrandRelationService;
import com.mrxiao.gulimall.product.service.CategoryService;
import com.mrxiao.gulimall.product.vo.Catelog2Vo;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

//    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

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
        System.out.println("★★★商品分类Service#");
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


        System.out.println("★★★商品分类结果#"+categoryEntity);
        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 1.检查当前删除的菜单，是否被其他引用
        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths=new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);
        return  parentPath.toArray(new Long[parentPath.size()]);
    }


    @Override
    public void updateCasecade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
    }

    /**
     * 1.每一个需要缓存的数据我们都指定要放到哪个名字的缓存。 【缓存的分区（按照业务类型分类）】
     * 2. @Cacheable({"category"})
     *   代表当前方法需要缓存，如果有，方法不用调用
     *   没有就调用,会调用方法，并将缓存的结果放入缓存
     * 3.默认行为
     *   1)如果命中缓存，方法不调用
     *   2) key默认字段生产
     *   3) 缓存的值，默认使用jdk序列化的数据
     *   4) 默认时间-1
     *
     * 自定义操作
     *  1)、指定生成的缓存使用的key:key属性指定，接受一个spEl
     *  2)、指定缓存的数据存活时间，配置文件中修改ttl
     *  3)、将数据保存成json格式
     *  4)
     *  2)
     *
     *
     * @return
     */
    //
    @Cacheable(value = {"category"},key = "'level1Category'")
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        long l = System.currentTimeMillis();
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        System.out.println("消费时间:"+(System.currentTimeMillis()-l));
        return categoryEntities;
    }

    @Override
    public  Map<String, List<Catelog2Vo>> getCategoryJson() {

        /**
         * 1.空结果缓存 :解决缓存穿透
         * 2.设置过期时间(加随机值)，解决缓存雪崩
         * 3.加锁:解决缓存击穿
         */

        String categoryJson = redisTemplate.opsForValue().get("categoryJson");
        if(StringUtils.isBlank(categoryJson)){
            System.out.println("缓存不命中。。。查询数据库");
            Map<String, List<Catelog2Vo>> categoryJsonFormDb = getCategoryJsonFormDbWithRedisLock();
            return categoryJsonFormDb;
        }
        System.out.println("缓存命中。。。直接返回");
        Map<String, List<Catelog2Vo>> stringListMap = JSON.parseObject(categoryJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {});
        return stringListMap;
    }

    /**
     * 缓存里面数据如何和数据库数据一致(缓存一致性)
     * 双写模式 更改数据，把缓存数据更新
     * 失效模式 更改数据，把缓存删除，重新查询数据
     * @return
     */
    public  Map<String, List<Catelog2Vo>> getCategoryJsonFormDbWithRedissonLock() {
        //1、锁的名字，锁的粒度，越细越快
        RLock lock = redissonClient.getLock("CategoryJson-lock");
        lock.lock();
        //加锁成功执行业务
        Map<String, List<Catelog2Vo>> dataFromDb = null;
        try {
            dataFromDb = getDataFromDb();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return dataFromDb;
    }

    public  Map<String, List<Catelog2Vo>> getCategoryJsonFormDbWithRedisLock() {
        //1.占分布式锁
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid,300,TimeUnit.SECONDS);
        if(lock){
            System.out.println("获取分布式锁成功");
            //加锁成功执行业务
            Map<String, List<Catelog2Vo>> dataFromDb = null;
            try {
                dataFromDb = getDataFromDb();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                String script="if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                Long lock1 = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), uuid);

            }

            //获取值+对比值，删除
//            String lockValue = redisTemplate.opsForValue().get("lock");
//            if(uuid.equals(lockValue)){
//                redisTemplate.delete("lock");
//            }

            return dataFromDb;
        }else{
            System.out.println("火气分布式锁失败");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCategoryJsonFormDbWithRedisLock();
        }
    }

    private Map<String, List<Catelog2Vo>> getDataFromDb() {
        String categoryJson = redisTemplate.opsForValue().get("categoryJson");
        if (StringUtils.isNotBlank(categoryJson)) {
            Map<String, List<Catelog2Vo>> stringListMap = JSON.parseObject(categoryJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
            return stringListMap;
        }
        System.out.println("查询数据库selectList。。" + Thread.currentThread().getName());
        /**
         *  1、将数据库多次查询改成一次
         *
         **/
        List<CategoryEntity> entityList = baseMapper.selectList(null);

        //1.查出所有1级分类
        List<CategoryEntity> level1Categorys = getParent_cid(entityList, 0L);
        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            List<CategoryEntity> categoryEntities = getParent_cid(entityList, v.getCatId());
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(item -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, item.getCatId().toString(), item.getName());
                    //
                    List<CategoryEntity> level3Catelog = getParent_cid(entityList, item.getCatId());
                    if (level3Catelog != null) {
                        List<Catelog2Vo.Catelog3Vo> catelog3VoList = level3Catelog.stream().map(l3 -> {
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(item.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(catelog3VoList);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        String s = JSON.toJSONString(parent_cid);
        redisTemplate.opsForValue().set("categoryJson", s, 1, TimeUnit.DAYS);
        return parent_cid;
    }

    //从数据查询并封装分类数据
    public  Map<String, List<Catelog2Vo>> getCategoryJsonFormDbWithLocalLock() {
        //只要是同一把锁，就能锁住需要这个锁的所有线程
        //1.synchronized (this) :Springboot所有组件在容器中都是单例的
        //TODO 本地锁 synchronized
        synchronized (this){
            return getDataFromDb();
        }
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> categoryEntities,Long parent_cid) {

        List<CategoryEntity> collect = categoryEntities.stream().filter(item -> item.getParentCid().equals(parent_cid) ).collect(Collectors.toList());
        return collect;
//        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", item.getCatId()));
    }

    private List<Long> findParentPath(Long catelogId,List<Long> paths){
        //1.收集当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        System.out.println("findParentPath"+byId);
        if(byId.getParentCid()!=0)
        {
            findParentPath(byId.getParentCid(),paths);
        }
        return paths;
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
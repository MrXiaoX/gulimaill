package com.mrxiao.gulimall.product;

import com.mrxiao.gulimall.product.service.BrandService;
import com.mrxiao.gulimall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;

import java.util.Arrays;
import java.util.UUID;


@Slf4j
@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Test
    public void testStringRedisson(){
        System.out.println(redissonClient);
    }

    @Test
    public void testStringRedisTemplate(){

        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        ListOperations<String, String> stringStringListOperations = stringRedisTemplate.opsForList();
        HashOperations<String, Object, Object> stringObjectObjectHashOperations = stringRedisTemplate.opsForHash();
        SetOperations<String, String> stringStringSetOperations = stringRedisTemplate.opsForSet();
        stringStringListOperations.set("list1",1,"admin");
        stringStringListOperations.set("list1",2,"xiao");
        stringStringListOperations.set("list1",3,"liu");
        RedisOperations<String, String> list1 = stringStringListOperations.getOperations();
        stringStringValueOperations.set("hello","world_"+ UUID.randomUUID().toString());
        String hello = stringStringValueOperations.get("hello");
        System.out.println("保存数据"+hello);
        System.out.println("保存数据list1"+list1);
    }


    @Test
    public void testFindPaths(){
        Long[] catelogPath = categoryService.findCatelogPath(1444L);
        log.info("完整路径{}", Arrays.toString(catelogPath));
        System.out.println("完整路径"+Arrays.toString(catelogPath));
    }



}

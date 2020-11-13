package com.mrxiao.gulimall.product.web;

import com.mrxiao.gulimall.product.entity.CategoryEntity;
import com.mrxiao.gulimall.product.service.CategoryService;
import com.mrxiao.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 * @ClassName IndexController
 * @Description TODO
 * @Version 1.0
 * @date 2020/10/28 0028 19:50
 */
@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    RedissonClient redisson;

    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping({"/","/index.html"})
    public String indexPage(Model model){
        List<CategoryEntity> categoryEntities= categoryService.getLevel1Categorys();
        model.addAttribute("categorys",categoryEntities);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCategoryJson(){
        Map<String, List<Catelog2Vo>> json= categoryService.getCategoryJson();
        return json;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        //1。获取一把锁，只要名字相同，就是同一把锁
        RLock myLock = redisson.getLock("myLock");
        //2.加锁
        myLock.lock(); //阻塞式等待
        //1)、自动续期，如果业务时间长，运行期间会自动续上30s,不用担心业务时间长，锁自动过期被删除
        //2)、加锁业务只要完成，就不好续期，并30s之后自动解锁

        myLock.lock(10, TimeUnit.SECONDS); //10秒自动解锁，默认在30s以后自动删除
        /**
         * 问题：lock.lock(10, TimeUnit.SECONDS) 在锁到了以后不会自动续期
         * 1、如果我们传递了锁的超时时间，就发送给redis执行脚本，进行占锁，默认就是我们设置的时间
         * 2、如果我们未指定锁的超时时间，就使用 30*1000 ms 【lockWatchdogTimeout看门狗的默认时间】
         * 3、只要占锁成功，就会启动一个定时任务【重新给锁设置过期时间，新的过期时间就是看门狗的默认时间】每隔10s都会自动刷新重试机制
         * internalLockLeaseTime 【看门狗时间/3 s】
         *
         * 最佳实战
         * 1) lock.lock(10, TimeUnit.SECONDS);省略整个续期操作。手动解锁
         *
         **/
        try {
            System.out.println("加锁成功执行业务"+ Thread.currentThread().getName());
            Thread.sleep(10000);
        } catch (Exception e) {

        }finally {
            //解锁 假设解锁代码没有执行，redisson会不会死锁
            System.out.println("解锁》》"+ Thread.currentThread().getName());
            myLock.unlock();
        }
        return "hello";
    }

    /**
     * 保证一定能读到最新数据，修改期间，写锁是一个排他锁（互斥、共享），读锁是一个共享锁
     * 写锁没释放就必须等待
     * 读+读:就相当于无锁。只会在redis记录好，所以当前读锁。他们都会同时加锁
     * 写+读 ：等待些锁释放
     * 写+写：阻塞方式
     * 读+写 :有读锁，写锁也要等待
     * 只要有写锁存在就必须等待
     **/
    @GetMapping("/write")
    @ResponseBody
    public String write(){

        RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
        RLock rLock = readWriteLock.writeLock();
        String s = null;
        try {
            rLock.lock();
            s = UUID.randomUUID().toString();
            Thread.sleep(10000);
            redisTemplate.opsForValue().set("writeValue",s);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            rLock.unlock();
        }
        return s;
    }

    @GetMapping("/read")
    @ResponseBody
    public String read(){
        RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
        RLock rLock = readWriteLock.readLock();
        rLock.lock();
        String writeValue = null;
        try {
            writeValue = redisTemplate.opsForValue().get("writeValue");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            rLock.unlock();
        }
        return writeValue;
    }

    /**
     * 车位停车
     * 3车位
     * 信号流做分布式限流
     */

    @GetMapping("/park")
    @ResponseBody
    public String park() throws InterruptedException {
        RSemaphore park = redisson.getSemaphore("park");
        park.acquire();//获取一个信号量，占一个车位

        boolean b = park.tryAcquire();

        return "ok 停车成功";
    }

    @GetMapping("/go")
    @ResponseBody
    public String go() throws InterruptedException {
        RSemaphore park = redisson.getSemaphore("park");
        park.acquire(); //释放一个信号量，释放一个车位
        return "ok 开车";
    }

    /**
     * 放假 锁门
     * 1班没人，等待2班
     * 5个班全走完才锁门
     *
     */
    @GetMapping("/lockDoor")
    @ResponseBody
    public String lockDoor() throws InterruptedException {
        RCountDownLatch door = redisson.getCountDownLatch("door");
        door.trySetCount(5);
        door.await(); //等待闭锁都完成
        return "锁门 放假";
    }

    @GetMapping("/goHome/{id}")
    @ResponseBody
    public String goHome(@PathVariable("id") Long id){
        RCountDownLatch door = redisson.getCountDownLatch("door");
        door.countDown(); //计数-1
        return id+"班人走了";
    }

}

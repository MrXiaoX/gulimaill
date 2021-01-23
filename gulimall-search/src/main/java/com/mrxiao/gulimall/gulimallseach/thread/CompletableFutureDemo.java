package com.mrxiao.gulimall.gulimallseach.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Administrator
 * @ClassName CompletableFutureDemo
 * @Description TODO
 * @Version 1.0
 * @date 2020/12/8 0008 21:23
 */
public class CompletableFutureDemo {
    private static ExecutorService executor=Executors.newFixedThreadPool(10);
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main 开始");
//        CompletableFuture.runAsync(()->{
//            System.out.println("当前线程:"+Thread.currentThread().getName());
//            int i=10/2;
//            System.out.println("运行结果"+i);
//
//        },executor);

        /**
         * 方法执行完成后的感知
         */
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程:" + Thread.currentThread().getName());
//            int i = 10 / 0;
//            System.out.println("运行结果" + i);
//            return i;
//        }, executor).whenComplete((result,exception)->{
//            //虽然能返回异常，当时没办法修改返回数据
//            System.out.println("异步任务完成了。。。结果是"+result+"异常"+exception);
//        }).exceptionally(throwable ->
//                //可以感知异常，同事返回数据
//                10);

        /**
         * 方法执行完成后的处理
         */
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程:" + Thread.currentThread().getName());
//            int i = 10 / 0;
//            System.out.println("运行结果" + i);
//            return i;
//        }, executor).handle((result,exception)->{
//            if(result!=null){
//                return result*2;
//            }
//            if(exception!=null){
//                return 0;
//            }
//            return 0;
//        });

        /**
         * 线程串行化
         * 1. thenRunAsyn:不能获取到上一步返回值
         * .thenRunAsync(()->{
         *             System.out.println("任务2启动了");
         *         },executor);
         * 2.thenAcceptAsync ,能接受上一步返回值，但是没有返回值
         * 3.thenAcceptAsync ,能接受上一步返回值，并且带返回值
         */
//        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程:" + Thread.currentThread().getName());
//            int i = 10 / 10;
//            System.out.println("运行结果" + i);
//            return i;
//        }, executor).thenApplyAsync(result -> {
//            System.out.println("任务2启动了" + result);
//            return "结果:" + result;
//        }, executor);

        /**
         * 两个任务都执行完成，在执行任务3
         */
//        CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程1:>>" + Thread.currentThread().getName());
//            int i = 10 / 3;
//            System.out.println("运行结果1>>" + i);
//            return i;
//        }, executor);
//
//        CompletableFuture<Integer> future02 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程2:>>" + Thread.currentThread().getName());
//            int i = 10 / 4;
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println("运行结果2<<" + i);
//            return i;
//        }, executor);

//        future01.runAfterBothAsync(future02,()->{
//            System.out.println("当前线程3");
//        },executor);

//        future01.thenAcceptBothAsync(future02,(f1,f2)->{
//            System.out.println("任务3开始>>"+f1+" f2结果"+f2);
//        },executor);

//        CompletableFuture<String> future = future01.thenCombineAsync(future02, (f1, f2) -> f1 + ":->>" + f2, executor);


        /**
         * 两个任务,只要又一个任务执行完成，就执行任务3
         * runAfterEitherAsync :无感知结果，无返回值
         * acceptEitherAsync :有感知结果1，无返回值
         * applyToEitherAsync :有感知结果1，有返回值
         *
         */

//        future01.runAfterEitherAsync(future02,()->{
//            System.out.println("任务3开始 之前的结果");
//        },executor);


//        future01.acceptEitherAsync(future02,res->{
//            System.out.println("任务3开始 之前的任务2结果>>"+res);
//        },executor);

//        CompletableFuture<String> future = future01.applyToEitherAsync(future02, res -> {
//            System.out.println("任务3开始 之前的任务2结果>>" + res);
//            return res + "-->>任务3结束";
//        }, executor);

        CompletableFuture<String> futureImg = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的图片" );
            return "hello.jpg";
        }, executor);

        CompletableFuture<String> futureAttr = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的属性" );
            return "黑色+256G";
        }, executor);


        CompletableFuture<String> futureDesc = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的介绍" );
            return "华为";
        }, executor);

        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(futureImg, futureAttr, futureDesc);
        anyOf.get();

//        System.out.println("main 结束"+future.get());
        System.out.println("main 结束"+anyOf.get());
    }



}

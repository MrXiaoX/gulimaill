package com.mrxiao.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Administrator
 * @ClassName Catelog2Vo
 * @Description TODO
 * @Version 1.0
 * @date 2020/10/29 0029 19:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catelog2Vo {
   private String catalog1Id; //1.级父分类id
   private List<Catelog3Vo> catalog3List; //三级子分类
   private String id;
   private String name;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catelog3Vo{
        private String catalog2Id; //1.级父分类id
        private String id;
        private String name;
    }


}

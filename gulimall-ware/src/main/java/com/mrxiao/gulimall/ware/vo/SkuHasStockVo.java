package com.mrxiao.gulimall.ware.vo;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SkuHasStockVo {

   private Long skuId;

   private Boolean hasStock;
}

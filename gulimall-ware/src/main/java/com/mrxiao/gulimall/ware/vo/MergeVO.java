package com.mrxiao.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class MergeVO {
    private Long purchaseId;
    private List<Long> items;

}

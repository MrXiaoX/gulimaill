package com.mrxiao.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrxiao.common.constant.ProductConstant;
import com.mrxiao.common.to.SkuHasStockVo;
import com.mrxiao.common.to.SkuReductionTo;
import com.mrxiao.common.to.SpuBoundTo;
import com.mrxiao.common.to.es.SkuEsModel;
import com.mrxiao.common.utils.PageUtils;
import com.mrxiao.common.utils.Query;
import com.mrxiao.common.utils.R;
import com.mrxiao.gulimall.product.dao.SpuInfoDao;
import com.mrxiao.gulimall.product.entity.*;
import com.mrxiao.gulimall.product.feign.CouponFeignService;
import com.mrxiao.gulimall.product.feign.SearchFeignService;
import com.mrxiao.gulimall.product.feign.WareFeignService;
import com.mrxiao.gulimall.product.service.*;
import com.mrxiao.gulimall.product.vo.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    SpuImagesService spuImagesService;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService attrValueService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    BrandService brandService;


    @Autowired
    CategoryService categoryService;

    @Autowired
    WareFeignService wareFeignService;

    @Autowired
    SearchFeignService searchFeignService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {
        try{
            System.out.println("★★保存spu基本信息"+spuSaveVo);
            //1、保存spu基本信息 pms_spu_info
            SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
            BeanUtils.copyProperties(spuSaveVo,spuInfoEntity);
            spuInfoEntity.setCreateTime(new Date());
            spuInfoEntity.setUpdateTime(new Date());
            this.saveBaseSpuInfo(spuInfoEntity);


            //2、保存Spu的描述图片 pms_spu_info_desc
            List<String> decript = spuSaveVo.getDecript();
            SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
            spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
            spuInfoDescEntity.setDecript(String.join(",",decript));
            spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);
            //3、保存spu的图片集 pms_spu_images
            List<String> images = spuSaveVo.getImages();
            spuImagesService.saveImages(spuInfoEntity.getId(),images);
            //4、保存spu的规格参数;pms_product_attr_value
            List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
            List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
                ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
                valueEntity.setAttrId(attr.getAttrId());

                AttrEntity id = attrService.getById(attr.getAttrId());
                valueEntity.setAttrName(id.getAttrName());
                valueEntity.setAttrValue(attr.getAttrValues());
                valueEntity.setQuickShow(attr.getShowDesc());
                valueEntity.setSpuId(spuInfoEntity.getId());

                return valueEntity;
            }).collect(Collectors.toList());
            attrValueService.saveProductAttr(collect);


            //5、保存spu的积分信息；gulimall_sms->sms_spu_bounds
            Bounds bounds = spuSaveVo.getBounds();
            SpuBoundTo spuBoundTo = new SpuBoundTo();
            BeanUtils.copyProperties(bounds,spuBoundTo);
            spuBoundTo.setSpuId(spuInfoEntity.getId());
            R r = couponFeignService.saveSpuBounds(spuBoundTo);
            if(r.getCode() != 0){
                log.error("远程保存spu积分信息失败");
            }


//        //5、保存当前spu对应的所有sku信息；

            List<Skus> skus = spuSaveVo.getSkus();
            if(CollectionUtils.isNotEmpty(skus)){
                skus.forEach(item->{
                    String defaultImg = "";
                    for (Images image : item.getImages()) {
                        if(image.getDefaultImg() == 1){
                            defaultImg = image.getImgUrl();
                        }
                    }
                    //    private String skuName;
                    //    private BigDecimal price;
                    //    private String skuTitle;
                    //    private String skuSubtitle;
                    SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                    BeanUtils.copyProperties(item,skuInfoEntity);
                    skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                    skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                    skuInfoEntity.setSaleCount(0L);
                    skuInfoEntity.setSpuId(spuInfoEntity.getId());
                    skuInfoEntity.setSkuDefaultImg(defaultImg);
//                //5.1）、sku的基本信息；pms_sku_info
                    skuInfoService.saveSkuInfo(skuInfoEntity);

                    Long skuId = skuInfoEntity.getSkuId();

                    List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
                        SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                        skuImagesEntity.setSkuId(skuId);
                        skuImagesEntity.setImgUrl(img.getImgUrl());
                        skuImagesEntity.setDefaultImg(img.getDefaultImg());
                        return skuImagesEntity;
                    }).filter(entity->{
                        //返回true就是需要，false就是剔除
                        return StringUtils.isNotEmpty(entity.getImgUrl());
                    }).collect(Collectors.toList());
//                //5.2）、sku的图片信息；pms_sku_image
                    skuImagesService.saveBatch(imagesEntities);
//                //TODO 没有图片路径的无需保存
//
                    List<Attr> attr = item.getAttr();
                    List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(a -> {
                        SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
                        BeanUtils.copyProperties(a, attrValueEntity);
                        attrValueEntity.setSkuId(skuId);

                        return attrValueEntity;
                    }).collect(Collectors.toList());
//                //5.3）、sku的销售属性信息：pms_sku_sale_attr_value
                    skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);
//
//                // //5.4）、sku的优惠、满减等信息；gulimall_sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price
                    SkuReductionTo skuReductionTo = new SkuReductionTo();
                    BeanUtils.copyProperties(item,skuReductionTo);
                    skuReductionTo.setSkuId(skuId);
                    if(skuReductionTo.getFullCount() >0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1){
                        R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                        if(r1.getCode() != 0){
                            log.error("远程保存sku优惠信息失败");
                        }
                    }
                });
            }

        }catch (Exception e){
            System.out.println("★★保存spu基本信息异常"+e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        String key = MapUtils.getString(params, "key");
        if(StringUtils.isNotBlank(key)){
            wrapper.and((w)->w.eq("id",key).or().like("spu_name",key));
        }
        String status = MapUtils.getString(params, "status");
        if(StringUtils.isNotBlank(status)){
            wrapper.eq("publish_status",status);
        }
        String brandId = MapUtils.getString(params, "brandId");

        if(StringUtils.isNotBlank(brandId)){
            wrapper.eq("brand_id",brandId);
        }
        String catelogId = MapUtils.getString(params, "catelogId");

        if(StringUtils.isNotBlank(catelogId)){
            wrapper.eq("catalog_id",catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {

        //1.查询当前spuId对应的sku信息，品牌名称
        List<SkuInfoEntity> skuBySpuList = skuInfoService.getSkuBySpuId(spuId);

        // TODO 4、查询当前sku的所以可以被用来检索的规格属性
        List<ProductAttrValueEntity> productAttrValueEntities = attrValueService.baseAttrlistforspu(spuId);
        List<Long> attrIds = productAttrValueEntities.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());
        List<Long> searchAttrIds=attrService.selectSearchAttrIds(attrIds);

        Set<Long> setAttrIds=new HashSet<>(searchAttrIds);

        List<SkuEsModel.Attrs> attrsList= productAttrValueEntities.stream().filter(item -> setAttrIds.contains(item.getAttrId()))
                .map(item->{
                    SkuEsModel.Attrs attrs1=new SkuEsModel.Attrs();
                    BeanUtils.copyProperties(item,attrs1);
                    return attrs1;
                }).collect(Collectors.toList());

        List<Long> skuIdList = skuBySpuList.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        //TODO 1、发送远程调用，库存系统查询是否有库存
        Map<Long, Boolean> stockMap=null;
        try {
            R<List<SkuHasStockVo>> skuHasStock = wareFeignService.getSkuHasStock(skuIdList);
            stockMap= skuHasStock.getData().stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));

        }catch (Exception e){
            log.error("库存服务查询异常，原因{}",e);
        }

        //2、封装每个sku的信息
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel>  upProducts=skuBySpuList.stream().map(sku->{
            //组织需要的数据
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(sku,esModel);
            esModel.setSkuPrice(sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());
            //设置库存信息
            if(finalStockMap==null){
                esModel.setHasStock(true);
            }else{
                esModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }
            //TODO 2、热度评分
            esModel.setHotScore(0L);
            // TODO 3、查询品牌和分类的名字和信息
            BrandEntity brand = brandService.getById(esModel.getBrandId());
            esModel.setBrandName(brand.getName());
            esModel.setBrandImg(brand.getLogo());

            CategoryEntity categoryEntity = categoryService.getById(esModel.getCatalogId());
            esModel.setCatalogName(categoryEntity.getName());
            //设置检索属性
            esModel.setAttrs(attrsList);
            return esModel;
        }).collect(Collectors.toList());

        // TODO 5、将数据发给es进行保存

        R r = searchFeignService.productStatusUp(upProducts);
        if(r.getCode()==0){
            //远程调用成功
            //TODO 6、修改当前spu状态
            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        }else{
            //远程调用失败
            //TODO 7、接口幂等性;重试机制
            //Feign调用过程
            /**
             *
             * 1、构造请求数据，将对象转为json
             *  RequestTemplate template = buildTemplateFromArgs.create(argv);
             * 2、发送请求进行执行（执行成功会解码响应数据）
             *  return executeAndDecode(template, options);
             * 3、执行请求会有重试机制
             * while (true) {
             *       try {
             *         return executeAndDecode(template, options);
             *       } catch (RetryableException e) {
             *         try {
             *           retryer.continueOrPropagate(e);
             *         } catch (RetryableException th) {
             *           Throwable cause = th.getCause();
             *           if (propagationPolicy == UNWRAP && cause != null) {
             *             throw cause;
             *           } else {
             *             throw th;
             *           }
             *         }
             *         if (logLevel != Logger.Level.NONE) {
             *           logger.logRetry(metadata.configKey(), logLevel);
             *         }
             *         continue;
             *       }
             *     }
             */
        }
    }

}
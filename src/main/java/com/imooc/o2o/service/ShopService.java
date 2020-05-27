package com.imooc.o2o.service;

import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.Shop;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.InputStream;

public interface ShopService {
    /**
     * 根据shopCondition分页返回相应店铺列表
     * @param shopCondition
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public ShopExecution getShopList(Shop shopCondition,int pageIndex,
                                     int pageSize);

    /**
     * 查询指定店铺信息
     * @param shopId
     * @return
     */
    Shop getByShopId(long shopId);

    /**
     * 更新店铺信息(从店家角度)
     * @param shop
     * @param shopImg
     * @return
     * @throws RuntimeException
     */
    ShopExecution modifyShop(Shop shop, ImageHolder thumbnail) throws RuntimeException;

    public ShopExecution addShop(Shop shop, ImageHolder thumbnail);
}
